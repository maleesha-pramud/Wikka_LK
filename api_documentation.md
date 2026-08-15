# Wikka_LK API Documentation

This guide provides frontend developers with the necessary details to consume the Wikka_LK backend APIs.

> [!NOTE]
> **Base URL:** The paths below are relative to your backend's API root. If you are running locally on Tomcat, your base URL might look like `http://localhost:8080/Wikka_LK-1.0/api` (or similar depending on your server configuration). Ensure you prepend this base URL to the endpoints.

---

## 1. Brand API

Manage product brands.

### `POST /brand`
**Description:** Add a new brand.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "Apple"
}
```

### `GET /brand`
**Description:** Fetch all brands.
**Response:** Returns a JSON object containing a `data` array of brands.

### `PUT /brand`
**Description:** Update an existing brand.
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id` (The ID of the brand to update)
**URL Example:** `PUT /brand?id=1`
**Body:**
```json
{
  "name": "Apple Updated"
}
```

### `DELETE /brand`
**Description:** Delete a brand.
**Query Parameters:** `id` (The ID of the brand to delete)
**URL Example:** `DELETE /brand?id=1`

---

## 2. Category API

Manage product categories.

### `POST /category`
**Description:** Add a new category.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "Electronics",
  "icon": "fa-desktop"
}
```

### `GET /category`
**Description:** Fetch all categories.
**Response:** Returns a JSON object containing a list of categories.

### `PUT /category`
**Description:** Update an existing category.
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id`
**URL Example:** `PUT /category?id=1`
**Body:**
```json
{
  "name": "Home Appliances",
  "icon": "fa-home"
}
```

### `DELETE /category`
**Description:** Delete a category.
**Query Parameters:** `id`
**URL Example:** `DELETE /category?id=1`

---

## 3. Color API

Manage product colors.

### `POST /color`
**Description:** Add a new color.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "Space Gray",
  "hexCode": "#343d46"
}
```

### `GET /color`
**Description:** Fetch all colors.
**Response:** Returns a JSON object with color details.

### `PUT /color`
**Description:** Update an existing color.
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id`
**URL Example:** `PUT /color?id=1`
**Body:**
```json
{
  "name": "Silver",
  "hexCode": "#c0c0c0"
}
```

### `DELETE /color`
**Description:** Delete a color.
**Query Parameters:** `id`
**URL Example:** `DELETE /color?id=1`

---

## 4. Model API

Manage product models.

### `POST /model`
**Description:** Add a new model associated with a brand.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "iPhone 15",
  "brandId": 1
}
```

### `GET /model`
**Description:** Fetch all models.
**Response:** Returns a JSON object with model data.

### `GET /model/brand/{brandId}`
**Description:** Fetch all models belonging to a specific brand.
**Path Parameters:** `brandId` (The ID of the brand)
**URL Example:** `GET /model/brand/1`
**Response:** Returns a JSON object containing a `data` array of the brand's models.

### `PUT /model`
**Description:** Update an existing model.
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id`
**URL Example:** `PUT /model?id=1`
**Body:**
```json
{
  "name": "iPhone 15 Pro Max",
  "brandId": 1
}
```

### `DELETE /model`
**Description:** Delete a model.
**Query Parameters:** `id`
**URL Example:** `DELETE /model?id=1`

---

## 5. Product API

Manage products.

### `POST /product`
**Description:** Create a new product.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "MacBook Air M2",
  "basePrice": 999.99,
  "description": "Supercharged by M2 chip.",
  "contactNo": "1234567890",
  "modelId": 2,
  "productConditionId": 1,
  "categoryId": 1,
  "userId": 1,
  "statusId": 1
}
```
**Response:** Returns a JSON object with the generated product ID in the `data` object (e.g., `{"status":true, "message":"...", "data":{"id":12}}`).

### `GET /product/all`
**Description:** Fetch all products.
**Response:** Returns a JSON object with product data.

### `GET /product/seller`
**Description:** Fetch all products for a specific seller (user).
**Query Parameters:** `userId` (The ID of the user/seller)
**URL Example:** `GET /product/seller?userId=1`
**Response:** Returns a JSON object containing a `data` array of the seller's products.

### `GET /product/`
**Description:** View a specific product's details.
**Query Parameters:** `id` (or similar identifier depending on implementation)
**URL Example:** `GET /product/?id=10`

### `PUT /product/update`
**Description:** Update an existing product.
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id` (Usually provided as a query string or inferred from request context)
**URL Example:** `PUT /product/update?id=10`
**Body:**
```json
{
  "name": "MacBook Pro M3",
  "basePrice": 1299.99,
  "description": "Mind-blowing speed.",
  "contactNo": "1234567890",
  "modelId": 3,
  "productConditionId": 1,
  "categoryId": 1,
  "userId": 1,
  "statusId": 1
}
```

### `PUT /product/change-status`
**Description:** Change the status of a product (e.g., active, inactive).
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "id": 10,
  "statusId": 2
}
```

### `DELETE /product/delete`
**Description:** Delete a product.
**Query Parameters:** `id`
**URL Example:** `DELETE /product/delete?id=10`

### `POST /product/{id}/image`
**Description:** Upload an image for a specific product. The first image uploaded will automatically become the main/primary image.
**Headers:** `Content-Type: multipart/form-data`
**Path Parameters:** `id` (The ID of the product)
**Form Data:** `file` (The image file)
**URL Example:** `POST /product/1/image`
**Response:** Returns a JSON object with the saved image details.

### `PUT /product/{id}/image/{imageId}/main`
**Description:** Set an existing product image as the main/primary image.
**Path Parameters:** `id` (The ID of the product), `imageId` (The ID of the image)
**URL Example:** `PUT /product/1/image/5/main`
**Response:** Returns success message if updated.

### `DELETE /product/{id}/image/{imageId}`
**Description:** Delete a specific image from a product. If you delete the main image, another image (if any) will automatically be set as the main image.
**Path Parameters:** `id` (The ID of the product), `imageId` (The ID of the image)
**URL Example:** `DELETE /product/1/image/5`
**Response:** Returns success message if deleted.

---

## 6. User API

Manage user authentication and profiles.

### `POST /user`
**Description:** Create a new user (Registration).
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123!",
  "address": "123 Main St, City, Country",
  "description": "Software Developer"
}
```

### `POST /user/activate`
**Description:** Activate a user account.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "email": "john@example.com",
  "verificationCode": "123456"
}
```

### `POST /user/login`
**Description:** Authenticate a user and create a session.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123!"
}
```

### `GET /user`
**Description:** Fetch logged-in user data. Uses session data; usually no body or parameters required.
**Response:** Returns a JSON object with the user's profile details.

### `GET /user/all`
**Description:** Fetch all registered users.

### `PUT /user`
**Description:** Update a user profile. Requires the user to be logged in (annotated with `@IsLoggedIn`).
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id` (or inferred from session)
**Body:**
```json
{
  "name": "John Doe Jr.",
  "address": "456 Side St",
  "description": "Senior Software Developer"
}
```

### `PUT /user/change-status`
**Description:** Change a user's status. Requires the user to be logged in.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "id": 1,
  "statusId": 2
}
```

### `GET /user/logout`
**Description:** Log out the currently authenticated user. Requires the user to be logged in.

---

## 7. Product Condition API

Manage product conditions (e.g., Brand New, Used).

### `POST /product-condition`
**Description:** Add a new product condition.
**Headers:** `Content-Type: application/json`
**Body:**
```json
{
  "name": "Brand New"
}
```

### `GET /product-condition`
**Description:** Fetch all product conditions.
**Response:** Returns a JSON object containing a `data` array of product conditions.

### `PUT /product-condition`
**Description:** Update an existing product condition.
**Headers:** `Content-Type: application/json`
**Query Parameters:** `id`
**URL Example:** `PUT /product-condition?id=1`
**Body:**
```json
{
  "name": "Used - Like New"
}
```

### `DELETE /product-condition`
**Description:** Delete a product condition.
**Query Parameters:** `id`
**URL Example:** `DELETE /product-condition?id=1`

---

## 8. Test API

Check API Health.

### `GET /test`
**Description:** A simple health check to verify the API is reachable.
**Response:** `{"status": true}`
