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
### 📸 Planned Screenshots (To Be Added Later)
Client list view

Invoice PDF preview

Email sent confirmation

Mark as Paid toggle or button

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
