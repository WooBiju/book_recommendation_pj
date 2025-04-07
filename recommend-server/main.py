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

class RecommendRequestDTO(BaseModel):
    preferredGenres: List[str]  # 유저의 선호 장르 리스트
    favoriteBookIds: List[int]  # 유저의 찜 목록 리스트
    bookInfos: List[BookInfoDTO]

# 응답 DTO
class RecommendResultDTO(BaseModel):
    bookIds: List[int]

# 장르 -> One-hot 벡터로 변환
def genre_to_vector(genre: str) -> List[int]:
    return [1 if genre.upper() == g else 0 for g in ALL_GENRES]

# 사용자 벡터 생성 : 선호 장르 + 찜 도서 장르 평균
def build_user_vector(preferredGenres: List[str], favoriteBookIds: List[int], bookInfos: List[BookInfoDTO]) -> np.ndarray:
    genre_vectors = [genre_to_vector(g) for g in preferredGenres]

    # 찜한 도서들 장르 찾기
    favorite_genres = []
    book_map = {b.id:b.genre for b in bookInfos}
    for book_id in favoriteBookIds:
        genre = book_map.get(book_id)
        if genre:
            favorite_genres.append(genre)

    favorite_vectors = [genre_to_vector(g) for g in favorite_genres]

    all_vectors = genre_vectors + favorite_vectors
    if not all_vectors:
        return np.zeros((1,len(ALL_GENRES)))
    return np.mean(all_vectors,axis=0).reshape(1,-1)

# 모든 도서 벡터화
def build_book_vectors(bookInfos: List[BookInfoDTO]) -> Dict[int, np.ndarray]:
    vectors = {}
    for book in bookInfos:
        vectors[book.id] = np.array(genre_to_vector(book.genre)).reshape(1,-1)
    return vectors


@app.post("/recommend",response_model=RecommendResultDTO)
def recommend(request: RecommendRequestDTO):
    print("✅ FastAPI 요청 들어옴!")
    print(f"받은 장르 목록: {request.preferredGenres}")
    print(f"찜 도서: {request.favoriteBookIds}")
    print(f"전체 도서 수: {len(request.bookInfos)}")

    user_vector = build_user_vector(request.preferredGenres,request.favoriteBookIds,request.bookInfos)
    book_vectors = build_book_vectors(request.bookInfos)

    scores = []
    for book_id, vector in book_vectors.items():
        sim = cosine_similarity(user_vector,vector)[0][0]
        scores.append((book_id,sim))

    # 유사도 기준 정렬 후 상위 10개 추천
    scores.sort(key=lambda x: x[1], reverse=True)
    top_books = [book_id for book_id,_ in scores[:10]]

    return RecommendResultDTO(bookIds=top_books)