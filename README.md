# Food Delivery Backend

A Spring Boot REST API backend for a food delivery platform built with clean architecture principles.

## Features

### Implemented APIs

#### 1. User Authentication & Management
- ✅ User Registration (`POST /api/auth/signup`)
- ✅ User Login (`POST /api/auth/login`)
- ✅ Guest Sessions (`POST /api/auth/guest-session`)
- ✅ User Profile Management (`GET/PUT /api/users/profile`)
- ✅ Address Management (`POST/PUT/DELETE/PATCH /api/users/addresses`)

#### 2. Restaurant & Menu APIs (To be implemented)
- Search Restaurants
- Get Restaurant Details & Menu
- Menu Management for Restaurant Owners

#### 3. Order Management APIs (To be implemented)
- Cart Management
- Order Placement
- Order Tracking
- Restaurant Order Management

#### 4. Delivery Management APIs (To be implemented)
- Rider Assignment
- Delivery Tracking
- Rider Interface

## Architecture

This project follows **Clean Architecture** principles with the following layers:

### Domain Layer (`/domain`)
- **Models**: Core business entities (User, Restaurant, Order, etc.)
- Pure business logic with no external dependencies

### Application Layer (`/application`) 
- **Services**: Business logic implementation
- **Ports**: Repository interfaces (dependency inversion)
- No framework dependencies

### Infrastructure Layer (`/infrastructure`)
- **Controllers**: REST API endpoints and DTOs
- **Persistence**: MongoDB repositories and adapters
- **Config**: Spring configuration and beans

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: MongoDB
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Java Version**: 17

## Database Schema

### Collections:
1. **users** - User accounts and profiles
2. **guest_sessions** - Temporary guest sessions
3. **restaurants** - Restaurant information
4. **menu_items** - Restaurant menu items
5. **carts** - User shopping carts
6. **orders** - Order information and tracking
7. **delivery_riders** - Delivery personnel

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+
- MongoDB 4.4+

### Configuration

1. **Database Configuration**
   
   Update `src/main/resources/application.properties`:
   ```properties
   # Replace with your actual MongoDB URI
   spring.data.mongodb.uri=mongodb://localhost:27017/food_delivery_db
   ```

2. **JWT Configuration**
   
   Replace the placeholder JWT secret:
   ```properties
   jwt.secret=your-actual-256-bit-secret-key-here
   ```

### Running the Application

1. **Clone and navigate to project**
   ```bash
   cd /home/tashrif/Desktop/food-delivery-backend
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Application will start on**: `http://localhost:8080/api`

## API Documentation

### Authentication APIs

#### Sign Up
```http
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com", 
  "password": "securePassword"
}
```

#### Guest Session
```http
POST /api/auth/guest-session
```

### Profile Management APIs

#### Update Profile
```http
PUT /api/users/profile?userId={userId}
Content-Type: application/json

{
  "name": "John Doe",
  "phone": "+8801234567890"
}
```

#### Add Address
```http
POST /api/users/addresses?userId={userId}
Content-Type: application/json

{
  "type": "HOME",
  "street": "123 Street",
  "city": "Dhaka",
  "area": "Gulshan",
  "isDefault": true
}
```

## Development Status

### ✅ Completed
- Clean Architecture Setup
- User Authentication & JWT
- User Profile Management (with proper DTOs)
- Address Management
- MongoDB Configuration
- Basic Error Handling
- DTO Mapping between layers

### 🚧 Next Steps
1. Restaurant & Menu Management
2. Cart & Order Management
3. Delivery Management
4. Payment Integration
5. Real-time Order Tracking
6. Analytics & Reporting

## Testing

Run tests with:
```bash
mvn test
```

## Contributing

1. Follow clean architecture principles
2. Add appropriate validation
3. Include unit tests
4. Update documentation

## Security Notes

- JWT tokens expire in 24 hours
- Passwords are encrypted using BCrypt
- Guest sessions expire in 1 hour
- CORS is configured for development

---

**Note**: This is the initial implementation focusing on user management. Additional features will be implemented incrementally following the same clean architecture pattern.
