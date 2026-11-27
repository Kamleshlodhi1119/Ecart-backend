# Ecart-Backend

🛠️ **eCart Backend Description (Spring Boot 3 + Maven)**  
This backend is a full-featured, scalable e-commerce platform built using Spring Boot 3 and Maven for dependency management and project build. It provides RESTful APIs to support secure user authentication, product management, cart operations, order processing, image uploads, and feedback handling — all fully integrated and ready for frontend communication via Angular 15.

---

## 🔑 Core Features

### ✅ 1. Authentication & Authorization
- Built with Spring Security and JWT-based authentication.  
- Implements role-based access control (USER, ADMIN).  
- APIs for user registration, login, and token validation.

### 🛍️ 2. Product Management
- Admin-only APIs to add, edit, or delete products.  
- Public APIs for product listing, filtering by category, keyword, or price.  
- Image upload support using MultipartFile.  
- Product model supports descriptions, pricing, stock, and multiple categories.

### 🛒 3. Shopping Cart API
- User cart is managed via session or user account.  
- Add, remove, update cart items.  
- Syncs cart data with the frontend for real-time updates.

### 🧾 4. Order System
- Process and persist orders with shipping and billing info.  
- Order summary includes item snapshots, price totals, status.  
- Admin can manage orders and update statuses (e.g., pending, shipped, delivered).

### 🖼️ 5. Image Upload
- Product images are validated and uploaded to Cloudinary over HTTPS.
- Uploaded images are stored in the Cloudinary `clothes` folder with a generated public ID.
- The returned secure URL is saved in the product's `imageUrl` field and included in product responses.
- Configure `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, and `CLOUDINARY_API_SECRET` as environment variables before starting the application. Do not commit Cloudinary credentials.
- Upload an image as multipart field `image` to `POST /api/products/{id}/upload-image` with a valid authentication token.

### 💬 6. Feedback / Ratings
- Users can submit product reviews and star ratings.  
- Admin can view/manage feedback.  
- Ratings auto-averaged and returned with product data.

---

## 🧰 Tech Stack

| Layer           | Technology                  |
|-----------------|-----------------------------|
| Backend Framework | Spring Boot 3               |
| Build Tool      | Maven                       |
| Security       | Spring Security + JWT        |
| Database       | MySQL / PostgreSQL           |
| ORM            | Hibernate / Spring Data JPA  |
| File Upload    | Spring MVC (MultipartFile)   |
| Documentation  | Swagger / OpenAPI            |
| Dependency Mgmt | Maven                       |
| Dev Tools      | Lombok, ModelMapper          |

---

## 📂 Maven Project Structure



## Live Images 

## Customer Pages
![Customer-Login](https://github.com/user-attachments/assets/bec06b5a-916b-40d6-b526-d5bf5d6b10a8)
![Customer_registeration](https://github.com/user-attachments/assets/a289457a-d256-46b3-8345-fce16f4e22b5)
![customer-home](https://github.com/user-attachments/assets/5ed5e161-8a7f-48de-8da4-8f37ae2f5f14)
![admin-products](https://github.com/user-attachments/assets/384d1bc5-a32f-4d89-a500-2f94716bf7aa)
![customer-cart](https://github.com/user-attachments/assets/745da165-ade7-46ba-8a03-e8056f4f60ad)
![customer-checkout](https://github.com/user-attachments/assets/e72ed490-ee20-415e-8e07-8f36743e2e18)
![customer-orders](https://github.com/user-attachments/assets/95021be1-0cd8-47bd-9704-09cb4b70fe80)
![customer-feedback](https://github.com/user-attachments/assets/d4ec5f1c-e4b4-47f4-8fa2-a974a67ba4a1)
![customer-profile](https://github.com/user-attachments/assets/a4714d38-6b3c-4b8d-ac71-a394755e25cf)
![customer-profile-2](https://github.com/user-attachments/assets/3270507d-f12d-4f71-aa38-afe9f749da96)

## Admin Pages
![Admin-Login](https://github.com/user-attachments/assets/f7381c4b-de0a-44b7-bd79-292bbc1633ad)
![admin-dashboard](https://github.com/user-attachments/assets/fe287ad1-fa15-464c-9172-21b64e561cc2)
![admin-orders](https://github.com/user-attachments/assets/fa704aac-49a9-4368-808a-7c456ee93a48)
![admin-feedbacks](https://github.com/user-attachments/assets/2e8a55a9-32ab-4459-a74f-19cf8ca04314)
![admin-customers](https://github.com/user-attachments/assets/29dd6427-93c4-4b67-b30c-ec09a24c3be2)
![search-product](https://github.com/user-attachments/assets/d5273657-c0a8-4d96-96cc-f673ef0b56bd)
