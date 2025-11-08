E-Wallet Microservices
1️⃣ System Overview
Goal: Build a secure, distributed e-wallet system supporting customers & merchants, with role-based JWT authentication, wallet balance management, fund transfer, fee deduction, transaction ledger, and notifications.
Architecture: Microservices – Wallet, Payment, Notification.
Tech Stack: Java 17, Spring Boot 3.x, H2/PostGre, JWT, Swagger, Maven, Docker Compose, Sonar, JUnit.
2️⃣ Microservice Responsibilities
Customer eWallet Service → Wallet Management (Customer Onboard, verify_otp,SetCredientails, login(JWT).
Payment Service → Transfer, Fee Calculation, Ledger, Notifications.
Notification Service → Merchant Notifications.


3️⃣ Database Design
Customer eWallet Service:
Customer(id,phone_number,email,mpin, Username, password,status)
Customer_account(id, customer_Id, balance,Account_number),
OTP(id,customer_id,otp,status)


Payment Service:
Transaction(id, customer_id, ben_id, amount, fee, status, timestamp),
Merchant(id,AccountNumber,phone, emailId)


Notification Service:
(id, customer_id, amount, status, timestamp,message)
Relations: User─<Wallet, User─<TransactionRecord.



4️⃣ API Contract Summary

Customer eWallet Service:
POST /api/customer/onboard → Register user
POST /api/otp/verifyOtp → otp verification
POST /api/customer/setCredentiale → set credential and create account in our ewallet
POST/api/customer/login -> customer will login to our eWallet system
GET / api/customer/dashboard -> get the details of customer and populate there details in dashboard

Payment Service:
POST /api/transaction/fundTransfer → Transfer funds (1% fee)
GET /api/transaction/{MerchantId} -> show Merchant
GET /api/transactions/{transactionId} → status of Transaction

Notification Service:
POST /api/notifiaction/send → Notification Trigger


5️⃣ Security Design
Authentication:  otp + JWT issued by Wallet Service
Authorization: Token required for secured endpoints

6️⃣ Inter-Service Communication

Payment→customer eWallet service: REST for balance validation + credit/debit
Payment→Notification: REST for merchant alerts
Future: Kafka for async processing

7️⃣ Fee & Ledger Logic
1% fee per transaction.
Example: ₹1000 transfer → Fee ₹10 → Merchant gets ₹990.
Transaction stored in PaymentService ledger table.

8️⃣ Logging & Exception Handling
Framework: SLF4J + Logback
INFO: Transaction Success
WARN: Insufficient balance
ERROR: JWT validation failed
ControllerAdvice returns JSON error:
{ "timestamp": "...", "error": "Insufficient balance", "status": 400 }


🔟 Sprint 0 Deliverables
✅ Architecture Diagram
✅ DB Design
✅ API Contract
✅ Security Design
✅ Logging Plan
✅ Exception Handler
