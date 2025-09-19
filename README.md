# LMS 
# 📚 نظام إدارة المكتبة | Library Management System

نظام لإدارة مكتبة يحتوي على إدارة الكتب، الأعضاء، والاستعارات، مع سجل تاريخي مبسط وواجهة عربية سهلة الاستخدام.

---

## ✨ المميزات
- **إدارة الكتب**: إضافة، تعديل، حذف، وعرض الكتب.  
- **إدارة الأعضاء**: تسجيل وإدارة بيانات الأعضاء.  
- **نظام الاستعارات**: استعارة الكتب وتسجيل الإرجاع.  
- **السجل التاريخي**: حفظ جميع الاستعارات السابقة.  
- **واجهة عربية**: دعم كامل للغة العربية.  

---

## 🛠️ التقنيات المستخدمة

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **Oracle Database** (يمكن استبدالها بقاعدة أخرى)

### Frontend
- **Thymeleaf**
- **Bootstrap 5**
- **Font Awesome**
- **HTML5 / CSS3**

### الأدوات
- **Maven**
- **Lombok**
- **Spring Boot DevTools**

---

## 📂 هيكل المشروع
src/
├── main/
│ ├── java/com/baha/oop/
│ │ ├── controller/ # المتحكمات
│ │ ├── model/ # النماذج (Entities)
│ │ ├── repository/ # المستودعات
│ │ ├── service/ # الخدمات
│ │ └── util/ # الأدوات المساعدة
│ ├── resources/
│ │ ├── static/ # الملفات الثابتة
│ │ ├── templates/ # قوالب Thymeleaf
│ │ └── application.properties
└── test/ # اختبارات مبدئية (غير مكتملة)

markdown
Copy code

---

## 🗃️ النماذج (Entities)

### 📖 Book
- `id` - المعرف  
- `title` - العنوان  
- `author` - المؤلف  
- `isbn` - الرقم الدولي  
- `publicationYear` - سنة النشر  
- `available` - حالة التوفر  

### 👥 Member
- `id` - المعرف  
- `name` - الاسم  
- `email` - البريد الإلكتروني  
- `phone` - الهاتف  
- `address` - العنوان  

### 🔄 Borrowing
- `id` - مفتاح مركب (bookId + memberId)  
- `book` - الكتاب  
- `member` - العضو  
- `borrowDate` - تاريخ الاستعارة  
- `returnDate` - تاريخ الإرجاع  

### 📋 BorrowingHistory
- `id` - المعرف  
- `title` - عنوان الكتاب  
- `member` - اسم العضو  
- `borrowDate` - تاريخ الاستعارة  
- `returnDate` - تاريخ الإرجاع  

---

## 🚀 كيفية التشغيل

### المتطلبات
- Java 17 أو أعلى  
- Maven 3.6+  
- Oracle Database (أو قاعدة أخرى)  

### خطوات التشغيل
```bash
# استنساخ المشروع
git clone <رابط-المشروع>
cd library-management-system

# تكوين قاعدة البيانات
# - أنشئ قاعدة جديدة
# - عدّل إعدادات الاتصال في application.properties

# تثبيت التبعيات
mvn clean install

# تشغيل التطبيق
mvn spring-boot:run
بعد التشغيل، افتح المتصفح على:

arduino
Copy code
http://localhost:8080
⚙️ إعدادات التكوين
application.properties

properties
Copy code
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=username
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

server.port=8080
👥 المطور
بها الدين - GitHub

📄 الرخصة
هذا المشروع مرخص تحت رخصة MIT.
انظر ملف LICENSE للمزيد.

ملاحظة: هذا المشروع لأغراض تعليمية وتجريبية، وقد يحتاج إلى تطوير إضافي (اختبارات، تقارير، نشر) قبل استخدامه في بيئة إنتاج.

yaml
Copy code

---

تحبني أخلي التوثيق **مختصر جدًا (صفحة واحدة)** زي المشاريع الصغيرة في GitHub، ولا نتركه **مفصل زي هذا** لكن بدون الميزات غير المنفذة؟




You said:
ملف ريدمي موقعه اين
