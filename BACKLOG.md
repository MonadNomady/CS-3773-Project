# Product Backlog: Online Grocery Portal

This document tracks the functional requirements, system tasks, and features categorized by status.

## User Roles Reference
* **Madison:** Server Administrator, Frontend Developer, Lead Architect
* **Georgie:** Systems Architect, Backend Logic Developer
* **Ire:** Quality Assurance (QA) Engineer, Backend Logic Developer
* **Leigh:** Quality Assurance (QA) Engineer, Backend Logic Developer

---

## Completed Tasks

### Infrastructure & Devops
* [x] Create GitHub repository and link local code
* [x] Set up repository access controls and SSH keys
* [x] Create production `Dockerfile` using Java 17
* [x] Set up MySQL database on Clever Cloud
* [x] Deploy live application on Render connected to the database

### Database & System Design
* [x] Design core database models using the team UML Class Diagram
* [x] Create skeleton JPA entity classes from the design diagrams
* [x] Turn on automated database schema updates (`ddl-auto=update`)
* [x] Create `data.sql` script to add sample grocery items to the database
* [x] Verify local IDE connects successfully to the cloud database

---

## In Progress

### Frontend Core
* [ ] Draft initial client portal UI layouts and wireframes (Madison)
* [ ] Establish global layout templates, components, and stylesheets (Madison)

### Backend Functionality
* [ ] Claim and execute **Customer** logic in `Customer.java` (TBD)
  * *Tasks: Registration system validation, authentication routing, address updates.*
* [ ] Claim and execute **Shopping Cart** logic in `ShoppingCart.java` (TBD)
  * *Tasks: Cart item arithmetic, item additions, safe list deletions, cart clearing functions, context subtotal recalculations.*
* [ ] Claim and execute **Order** logic in `Order.java` (TBD)
  * *Tasks: Apply localized 8.25% sales tax calculation, promotional discount string validations, state transitions, logging console summaries.*
* [ ] Claim and execute **Order History** logic in `OrderHistory.java` (TBD)
  * *Tasks: Multi-parameter lists retrievals, date-based comparisons, cost-based ascending/descending sorting methods.*

---

## Future Backlog Items

### Integration & Testing
* [ ] Conduct end-to-end integration tests tracking user sessions from cart additions to order creation

### Presentation Preparation
* [ ] Draft project slideshow framework mapped across team roles
* [ ] Structure 12-minute technical run-through and record full operational demo
