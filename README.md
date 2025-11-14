# 🐾 AimoPets - Full Stack Application

This project consists of:  

- **Backend:** Java Spring Boot (REST API)
- **Frontend:** React + Vite (TypeScript)
- **Dockerized:** Both backend and frontend can run using Docker and Docker Compose
- **Optional E2E:** Playwright tests
- **Github pipeline:** Run test case in git

---

## **Prerequisites**

Make sure you have installed:

- [Docker](https://www.docker.com/get-started) (v20+)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2+)
- Optional: [Node.js](https://nodejs.org/) and [Java 17](https://adoptium.net/) if you want to run locally without Docker

---

## **Run the Application using Docker**

1. **Clone the repository**

```bash
git clone <your-repo-url>
cd AimoPets
```

2. **Build and start containers**

```bash
docker-compose up --build
```
- This will build backend and frontend images.
- Backend will run on http://localhost:8080
- Frontend (served via Nginx) will run on http://localhost:5173
- Use http://localhost:5173 to load the applications

3. **Stop the application**

```bash
docker-compose down
```

## **Backend API**

**Endpoint**: GET /api/users-with-pet

Example request:

http://localhost:8080/api/users-with-pet?results=10&nat=US


**Query Parameters**:

- results → number of users to fetch (default: 10)
- nat → Code of nationality (e.g., US,GB,FI)

**Response example**:
```bash
[
  {
    "id": "7145588T",
    "gender": "male",
    "country": "FI",
    "name": "Kaylie Greenfelder",
    "email": "Greenfelder@hotmail.com",
    "dob": { "date": "1968-03-29T05:26:03.876Z", "age": 57 },
    "phone": "(743) 374-5564 x9928",
    "petImage": "https://images.dog.ceo/breeds/pariah-indian/The_Indian_Pa.jpg"
  }
]
```

## **Frontend**

- Open http://localhost:5173 in your browser

**Features**:

- Browse users with their pets
- Filter by nationality
- Specify number of users to fetch
- Pagination for large result sets in frontend
- Maximum result count 2000
- Responsive grid of cards with user info and pet images

## **Run E2E Tests (Optional)**

1. **Make sure Docker containers are running**:
```bash
docker-compose up --build -d
```

2. **Run the E2E tests (assuming tests are in /e2e folder)**:
```bash
cd e2e
npm run test
```