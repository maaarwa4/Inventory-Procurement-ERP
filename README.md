<a href="https://github.com/maaarwa4">
  <img width="100%" src="https://capsule-render.vercel.app/api?type=waving&color=0:0EA5E9,50:4F46E5,100:9333EA&height=190&section=header&text=Inventory%20%26%20Procurement%20ERP&fontSize=42&fontColor=ffffff&fontAlignY=38&desc=Stock%2C%20suppliers%20and%20purchase%20orders%20in%20one%20place&descSize=16&descAlignY=60&animation=fadeIn" alt="Inventory & Procurement ERP" />
</a>

<div align="center">

<a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" /></a>
<a href="https://www.java.com"><img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" /></a>
<a href="https://angular.dev"><img src="https://img.shields.io/badge/Angular_20-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" /></a>
<a href="https://www.postgresql.org"><img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" /></a>

</div>

<br>

<h2><img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Light%20Bulb.png" width="32" align="center" />&nbsp; Overview</h2>

An ERP built to manage **stock and purchasing** for a catalog of mobile devices, organized around three business modules. Built between June and September 2025, the project covered the full lifecycle:

- **Functional scoping**: requirements gathering, module definition and business rules
- **Design & development**: Spring Boot REST API and Angular front end
- **User acceptance testing** with end users

<br>

<h2><img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Gear.png" width="32" align="center" />&nbsp; Business Modules</h2>

<table>
<tr>
<td width="33%" valign="top">

**Product Catalog**

Reference data for smartphones, tablets, laptops, smartwatches and accessories: brand, model, color, storage, screen size, network type and price.

</td>
<td width="33%" valign="top">

**Suppliers**

Supplier portfolio management with search by name, city and country, and an active / inactive filter.

</td>
<td width="34%" valign="top">

**Purchase Orders**

Supply orders linked to a supplier and a product, with automatic total calculation and **PDF export**.

</td>
</tr>
</table>

**Purchase order lifecycle**

```
PENDING  ──►  APPROVED  ──►  DELIVERED
 created       validated       goods received
```

<br>

<h2><img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Laptop.png" width="32" align="center" />&nbsp; Architecture</h2>

```
┌─────────────────────┐    REST / JSON    ┌─────────────────────┐     JPA     ┌──────────────┐
│  Angular 20 + SSR   │ ────────────────► │  Spring Boot 3 API  │ ──────────► │  PostgreSQL  │
│  Angular Material   │ ◄──────────────── │  Services · DTOs    │ ◄────────── │              │
└─────────────────────┘                   └─────────────────────┘             └──────────────┘
```

- **Backend**: layered architecture (controllers, services, repositories, DTOs), business enums mapped to native PostgreSQL types, PDF generation with Apache PDFBox
- **Frontend**: feature-based Angular structure (`core`, `features`, `layouts`, `models`) with Angular Material and server-side rendering
- **Quality**: 24 unit and integration tests (JUnit 5, Mockito) running on an in-memory H2 database

<br>

<h2><img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Travel%20and%20places/Rocket.png" width="32" align="center" />&nbsp; REST API</h2>

| Resource | Endpoints |
|:---|:---|
| Products | `GET` `POST` `PUT` `DELETE` `/api/products` · `GET /api/products/dropdown-data` |
| Suppliers | `GET` `POST` `PUT` `DELETE` `/api/suppliers` · `GET /api/suppliers/search` |
| Purchase orders | `GET` `POST` `PUT` `DELETE` `/api/purchase-orders` · `GET /api/purchase-orders/{id}/pdf` |

<br>

<h2><img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Hammer%20and%20Wrench.png" width="32" align="center" />&nbsp; Getting Started</h2>

**Prerequisites:** Java 17, Node.js 20+, PostgreSQL

```bash
git clone https://github.com/maaarwa4/Inventory-Procurement-ERP.git
cd Inventory-Procurement-ERP
```

**Database**: create a PostgreSQL database and initialize the schema with `backend/BD.sql`.

**Backend**

```bash
cd backend
export DB_URL=jdbc:postgresql://localhost:5432/your_database
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
./mvnw spring-boot:run
```

**Frontend**

```bash
cd frontend
npm install
npm start
```

**Tests**

```bash
cd backend
./mvnw test
```

<br>

<div align="center">

Built by **Marwa Bounoua**

<a href="https://linkedin.com/in/marwa-bounoua-877300263"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white" alt="LinkedIn" /></a>
<a href="https://github.com/maaarwa4"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white" alt="GitHub" /></a>

</div>

<a href="https://github.com/maaarwa4">
  <img width="100%" src="https://capsule-render.vercel.app/api?type=waving&color=0:0EA5E9,50:4F46E5,100:9333EA&height=100&section=footer" alt="" />
</a>
