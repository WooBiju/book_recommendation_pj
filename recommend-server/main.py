from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict
from sklearn.metrics.pairwise import cosine_similarity  # 두 벡터의 유사도를 계산하는 함수
from fastapi.middleware.cors import CORSMiddleware  # CORS 설정
from sklearn.feature_extraction.text import TfidfVectorizer # 텍스트 데이터를 숫자로 바꾸는 키워드 추출기
from konlpy.tag import Okt  # 한국어 형태소 분석기
import numpy as np  # 벡터 연산, 정렬 등 수치 계산
from numpy import isnan



app = FastAPI()
okt = Okt() # 형태소 분석기 초기화

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=5000, reload=True)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 불용어 리스트
KOREAN_STOPWORDS = {
    "리뷰", "작성", "합니다", "책", "내용", "이야기", "정말", "너무",
    "그냥", "조금", "것", "때", "나", "을", "를", "은", "는", "이", "가",
    "하다", "되다", "읽다", "읽었", "있다", "없다", "한번", "꼭", "라면"
}

# 전체 장르 목록
ALL_GENRES = ["NOVEL","POETRY","SCIENCE","SOCIETY","SELF_DEVELOPMENT","ECONOMY","HISTORY","IT","ESSAY","TRAVEL"]

# 요청 DTO
class BookInfoDTO(BaseModel):
    id: int
    genre :str

class RatedBookDTO(BaseModel):
    id: int
    rating: float

class ReadingHistoryDTO(BaseModel):
    id: int
    genre : str
    progress : float
    status : str

class ReviewForKeywordDTO(BaseModel):
    reviewId: int
    bookId: int
    content: str

class BookForKeywordDTO(BaseModel):
    bookId: int
    description: str

class RecommendRequestDTO(BaseModel):
    preferredGenres: List[str]  # 유저의 선호 장르 리스트
    favoriteBooks: List[int]  # 유저의 찜 목록 리스트
    ratedBooks: List[RatedBookDTO] # 유저의 별점 목록 리스트
    readingHistory: List[ReadingHistoryDTO]  # 유저의 독서 기록 리스트
    bookInfos: List[BookInfoDTO]

# 응답 DTO
class RecommendResultDTO(BaseModel):
    bookIds: List[int]

# 장르 -> One-hot 벡터로 변환
def genre_to_vector(genre: str) -> List[int]:
    return [1 if genre.upper() == g else 0 for g in ALL_GENRES]

# 사용자 벡터 생성 : 선호 장르 + 찜 도서 장르 , 별점 4.0 이상 가중치 평균
def build_user_vector(preferredGenres, favoriteBooks, ratedBooks, readingHistory, bookInfos )-> np.ndarray:

    genre_vectors:List[np.ndarray] = [np.array(genre_to_vector(g)) for g in preferredGenres]  # 장르 벡터 변환

    # 찜한 도서들 장르 + 별점 기반 가중치 반영
    book_map = {b.id:b.genre for b in bookInfos}

    # 찜한 도서 (기본 가중치 1.2)
    for book_id in favoriteBooks:
        genre = book_map.get(book_id)
        if genre:
            vec = np.array(genre_to_vector(genre))
            genre_vectors.append(vec * 1.2)

    # 별점 기반 가중치
    for rated in ratedBooks:
        genre = book_map.get(rated.id)
        if genre:
            vec = np.array(genre_to_vector(genre))
            weight = get_rating_weight(rated.rating)
            genre_vectors.append(vec * weight)

    # 독서 기록 반영
    for read in readingHistory:
        vec = np.array(genre_to_vector(read.genre))
        if read.status.upper() == "READING":
            genre_vectors.append(vec * 1.1)
        elif read.status.upper() == "COMPLETED":
            genre_vectors.append(vec * 1.3)

    if not genre_vectors:
        return np.zeros((1,len(ALL_GENRES)))

    return np.mean(genre_vectors,axis=0).reshape(1,-1)

# 모든 도서 벡터화
def build_book_vectors(bookInfos: List[BookInfoDTO]) -> Dict[int, np.ndarray]:
    vectors = {}
    for book in bookInfos:
        vectors[book.id] = np.array(genre_to_vector(book.genre)).reshape(1,-1)
    return vectors

# 가중치 계산 함수
def get_rating_weight(rating: float) -> float:
    if rating >= 5.0:
        return 2.0
    elif rating >= 4.0:
        return 1.8
    elif rating >= 3.0:
        return 1.5
    elif rating >= 2.0:
        return 1.2
    else:
        return 1.0

@app.post("/recommend",response_model=RecommendResultDTO)
def recommend(request: RecommendRequestDTO):
    print("✅ FastAPI 요청 들어옴!")
    print(f"받은 장르 목록: {request.preferredGenres}")
    print(f"찜 도서: {request.favoriteBooks}")
    print(f"전체 도서 수: {len(request.bookInfos)}")

    user_vector = build_user_vector(request.preferredGenres,request.favoriteBooks,request.ratedBooks,request.readingHistory,request.bookInfos)
    book_vectors = build_book_vectors(request.bookInfos)

    scores = []
    for book_id, vector in book_vectors.items():
        sim = cosine_similarity(user_vector,vector)[0][0]
        print(f"Book ID: {book_id}, 유사도: {sim:.4f}")
        scores.append((book_id,sim))

    # 유사도 기준 정렬 후 상위 10개 추천
    scores.sort(key=lambda x: x[1], reverse=True)
    top_books = [book_id for book_id,_ in scores[:10]]

    print("최종 추천 도서:", top_books)

    return RecommendResultDTO(bookIds=top_books)

# 명사 추출 함수
def extract_korean_nouns(text: str) -> str:
    nouns = okt.nouns(text) # 명사만 추출
    print(f"[추출 전 명사] {nouns}")
    filtered = [n for n in nouns if n not in KOREAN_STOPWORDS and len(n) > 1]   # 불용어 리스트 + 한글자 제거
    print(f"[불용어 제거 후] {filtered}")
    return ' '.join(filtered)  # 공백 구분된 형태로 반환

@app.post("/recommend/keywords")
def recommend_by_keywords(reviews: List[ReviewForKeywordDTO], books: List[BookForKeywordDTO]):
        if not reviews:
            raise HTTPException(status_code=400, detail="리뷰 개수가 부족합니다.")

        # 1. 사용자 리뷰 키워드 누적
        user_text = " ".join([extract_korean_nouns(r.content) for r in reviews])
        print(f"[사용자 리뷰 전체 키워드] {user_text}")

        # 2. 각 도서 설명에서 키워드 추출
        book_ids = []
        book_texts = []

        for book in books:
            book_ids.append(book.bookId)
            book_texts.append(extract_korean_nouns(book.description or ""))

        # 3. TF - IDE 벡터화
        corpus = [user_text] + book_texts     # 사용자의 전체 리뷰 + 각 책의 키워드
        vectorizer = TfidfVectorizer(max_features=300)  # TF-IDF 점수 기준 상위 300개의 단어만 벡터로 사용
        tfidf_matrix = vectorizer.fit_transform(corpus)

        if tfidf_matrix.shape[1] == 0:  # 벡터의 단어 개수 0 이면
            raise HTTPException(status_code=400, detail="키워드를 추출하지 못했습니다.")

        # 4. 유사도 계산
        user_vec = tfidf_matrix[0]
        book_vecs = tfidf_matrix[1:]
        similarities = cosine_similarity(user_vec,book_vecs).flatten()
        sorted_indices = np.argsort(similarities)[::-1][:10]  # 유사도가 높은 순으로 정렬
        print(f"[유사도 벡터] {similarities}")

        # 5. 유효한 추천만 필터링
        recommend_book_ids = [
            book_ids[i]
            for i in sorted_indices
            if not np.isnan(similarities[i]) and similarities[i] >= 0.1
        ]

        print(f"[추천 도서 ID] {recommend_book_ids}")
        return {"bookIds" : recommend_book_ids}
