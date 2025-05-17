# 🧾 Smart Invoice

A Java Spring Boot application that helps freelancers and small businesses manage clients, create invoices, generate PDFs, and send invoices via email.

---

### 📌 Features

- ✅ Client & Product management (CRUD)
- ✅ Invoice generation with automatic totals
- ✅ PDF invoice generation using OpenPDF
- ✅ Email invoice with attachment
- ✅ Mark invoices as **paid/unpaid**
- ✅ Business logo & company info in PDFs
- ✅ Clean layered architecture with DTOs
- ✅ Built-in validation and exception handling

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
 ✅Client/Invoice/Product modules

 ✅PDF generation with branding

 ✅Email integration

 ✅Mark invoices as paid/unpaid

 ⬜Scheduled reminders

 ⬜Export as Excel/CSV

 ⬜Role-based authentication (Spring Security)

 ⬜Optional web interface (React or Angular)
