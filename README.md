# 🧾 Smart Invoice

A Java Spring Boot application that helps freelancers and small businesses manage clients, create invoices, generate PDFs, and send invoices via email.

---

### 📌 Features

## Backend Features
- CRUD for Clients, Products, and Invoices
- Filter/search endpoints for Clients, Products, Invoices
- CSV export for Invoices and Products
- PDF generation (OpenPDF)
- Email sending with PDF attachment
- Mark invoice as paid
- Automatic invoice number generation
- Scheduled email reminders
- Form login via Spring Security (session-based)
- Validation, exception handling, DTO usage

## Frontend Features (React + Vite)
🔒 Authentication
- Session-based login with /auth/login
- Protected routes with loading state
- Logout functionality with session invalidation

🧑 Clients Page

- Create, edit, delete clients (with modals)
- Filter/search by name, email, company
- Sort by name, city, or country
- Responsive table layout

📦 Products Page

- Create, edit, delete products (with modals)
- Live search by name
- Sorting by name and price (asc/desc)
- Error and loading state handling

🧾 Invoices Page

- Filter/search by invoice number
- Filter by paid/unpaid
- CSV export
- Create invoice
  - Dropdown for client selection
  - Modal with product checkbox selection
  - Pre-fill price and quantity
  - Inline editing of product price and quantity
  - Remove product from list
  - Total auto-calculation
  - Due date selection
  - Submit with validation
- View PDF
- Mark as paid
- Email invoice (PDF attachment)
- Delete invoice

---

### 🧱 Tech Stack

| Category          | Tools                              |
|------------------|-------------------------------------|
| Backend          | Java 17, Spring Boot                |
| Data Access      | Spring Data JPA, Hibernate          |
| Database         | PostgreSQL                          |
| PDF Generator    | OpenPDF                             |
| Email            | Spring Mail                         |
| Testing          | JUnit 5, Mockito                    |
| Build Tool       | Maven                               |

---

### 📂 Project Structure
```
src/
├── client/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── company
├── docs
├── exception
├── invoice/
│   ├── controller/
│   ├── dto/
│   ├── email/
│   ├── entity/
│   ├── pdf/
│   ├── repository/
│   └── service/
├── product/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── scheduler/
├── config/
├── util/
└── SmartInvoiceApplication.java
```

---

### 🛠️ Setup & Run

#### Prerequisites

- Java 17+
- PostgreSQL running locally
- Maven

#### 1. Clone the project

```bash
git clone https://github.com/ezzerof/smart-invoice-backend.git
cd smart-invoice-backend
```
#### 2. Configure PostgreSQL
Update application-local.yml:
```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_data_base
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver

  mail:
    username: your_email
    password: your_app_passcode

invoice:
  company:
    name: your_name
    address: your_address
    city: your_city
    country: your_country
    postcode: your_postcode
    phone: "your_phone_number"
    email: your_email
    bank:
      holder: bankc_card_holder_name
      account: "account_number"
      sort-code: "sort_code"

```
#### 3. Run the application
```
./mvnw spring-boot:run
```
### 📸 Planned Screenshots
<h4 align="center">Login Page</h4>
<p align="center">
  <img src="https://github.com/user-attachments/assets/218a1f21-df1a-4191-8a7d-a95678f2f86a" width="400"/>
</p>

<h4 align="center">Products Page</h4>
<p align="center">
  <img src="https://github.com/user-attachments/assets/d3d0dcde-adec-4a20-b304-bc6056439e81" width="400"/>
</p>

<h4 align="center">Client list view</h4>
<p align="center">
  <img src="https://github.com/user-attachments/assets/a8adcaf9-6355-4d53-b052-12733f4c2950" width="400"/>
</p>

<h4 align="center">Invoices Page</h4>
<p align="center">
  <img src="https://github.com/user-attachments/assets/55982b67-1a9a-4265-bf1d-4f3901c87f22" width="400"/>
</p>

<h4 align="center">Invoice PDF preview</h4>
<p align="center">
  <img src="https://github.com/user-attachments/assets/6a780ad6-031c-4cab-9779-e00d16ee9cbc" width="400"/>
</p>

<h4 align="center">Email sent confirmation</h4>
<p align="center">
  <img src="https://github.com/user-attachments/assets/d9c94dbe-a68b-4a87-8b26-9735d4ba9ad7" width="400"/>
</p>

### 🧪 Tests
Run tests with:
```
./mvnw test
```

### 🚧 Roadmap
- ✅ **Client / Invoice / Product modules** with full CRUD
- ✅ **PDF generation** with custom branding (logo, company info)
- ✅ **Email integration** to send invoices as PDF attachments
- ✅ **Mark invoices as paid/unpaid** via PATCH endpoint and frontend toggle
- ✅ **Scheduled reminders** (email) using Spring Scheduler
- ✅ **CSV export** for Invoices and Products
- ✅ **Authentication** using Spring Security (session-based login)
- ✅ [Optional] **React frontend (Vite)**:
  - Clients page with search, sort, modal forms
  - Products page with filters, sorting, modal forms
  - Invoices page with filtering, CSV export, PDF viewing, email, marking paid
  - Invoice creation modal:
    - Client dropdown
    - Product selection modal with checkbox picker
    - Inline price/quantity editing
    - Auto total calculation
    - Due date selector
    - Invoice number auto-generated
