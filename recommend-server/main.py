from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from fastapi.middleware.cors import CORSMiddleware


app = FastAPI()

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

# 전체 장르 목록
ALL_GENRES = ["NOVEL","POETRY","SCIENCE","SOCIETY","SELF_DEVELOPMENT","ECONOMY","HISTORY","IT","ESSAY","TRAVEL"]

# 요청 DTO
class BookInfoDTO(BaseModel):
    id: int
    genre :str

class RatedBookDTO(BaseModel):
    id: int
    rating: float

class RecommendRequestDTO(BaseModel):
    preferredGenres: List[str]  # 유저의 선호 장르 리스트
    favoriteBooks: List[int]  # 유저의 찜 목록 리스트
    ratedBooks: List[RatedBookDTO] # 유저의 별점 목록 리스트
    bookInfos: List[BookInfoDTO]

# 응답 DTO
class RecommendResultDTO(BaseModel):
    bookIds: List[int]

# 장르 -> One-hot 벡터로 변환
def genre_to_vector(genre: str) -> List[int]:
    return [1 if genre.upper() == g else 0 for g in ALL_GENRES]

# 사용자 벡터 생성 : 선호 장르 + 찜 도서 장르 , 별점 4.0 이상 가중치 평균
def build_user_vector(preferredGenres, favoriteBooks, ratedBooks, bookInfos ) -> np.ndarray:

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

    user_vector = build_user_vector(request.preferredGenres,request.favoriteBooks,request.ratedBooks,request.bookInfos)
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