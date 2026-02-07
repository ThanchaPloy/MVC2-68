# MVC2-68 Project

เลือกทำข้อ 1

### a) หน้าที่ของไฟล์และการทำงานร่วมกันใน MVC

**หลักการทำงานร่วมกัน**
- **Model**: ศูนย์รวมข้อมูลและ Business Logic จัดการการอ่าน-เขียนไฟล์ CSV ผ่าน Repository คลาส
- **View**: หน้าจอ GUI (Swing) สำหรับรับ Input และแสดงผลข้อมูล โดยไม่ติดต่อไฟล์โดยตรง แต่รอรับข้อมูลมาอัปเดตคอมโพเนนต์
- **Controller**: ตัวกลางประสานงาน ดักฟังเหตุการณ์ (Listener) เพื่อสั่งการ Model/Service และอัปเดตผลลัพธ์กลับไปยัง View

#### *** Main ***
- **Main.java**: จุดเริ่มต้นของโปรแกรม ทำหน้าที่สร้างข้อมูลตั้งต้น (Seed Data) หากยังไม่มีไฟล์ CSV, ติดตั้ง Model (Repositories/Service) และสร้างหน้าจอหลัก (RumourListView) พร้อมเชื่อมต่อกับ Controller เริ่มต้น

#### *** Model ***
- **RumourService.java**: หัวใจของระบบ เก็บกฎธุรกิจ เช่น การคำนวณ Hot Score, ตรวจสอบการรายงานซ้ำ, เปลี่ยนสถานะเป็น PANIC เมื่อยอดรายงาน >= 3, และการตรวจสอบสิทธิ์ของผู้ตรวจสอบ (Verifier)
- **CsvRumourRepository.java / CsvReportRepository.java / CsvUserRepository.java**: ทำหน้าที่เป็นส่วนจัดการไฟล์ (Persistence) เพื่ออ่านและบันทึกข้อมูลข่าวลือ, รายงาน, และผู้ใช้ ลงในไฟล์ .csv
- **EventBus.java**: ระบบแจ้งเตือนภายใน (Observer Pattern) เพื่อให้หน้าสรุปผล (Summary) รีเฟรชข้อมูลอัตโนมัติทันทีที่มีการ Report หรือ Verify
- **Rumour.java / Report.java / User.java**: คลาสข้อมูล (domain objects) ที่เก็บโครงสร้างคอลัมน์ตามที่โจทย์กำหนด

#### *** Controller ***
- **RumourListController.java**: ควบคุมหน้าจอรายการข่าวขึง การเรียงลำดับ (Sort), การกรองข่าวที่ Verify แล้ว (Filter), และการเปิดไปหน้าแสดงรายละเอียด
- **RumourDetailController.java**: ควบคุมหน้ารายละเอียดข่าวลือ จัดการการกด Report และการกด Verify โดยตรวจสอบสิทธิ์ผู้ใช้ (Role) ก่อนดำเนินการ
- **SummaryController.java**: ควบคุมหน้าสรุปผล จัดการการโหลดข้อมูลข่าวที่ติด PANIC และข่าวที่ผ่านการตรวจสอบแล้ว โดยจะรีเฟรชตัวเองอัตโนมัติผ่าน EventBus

#### *** View ***
- **RumourListView.java**: แสดงตารางรวมข่าวลือ (JTable) พร้อมฟิลเตอร์การเรียงลำดับและสถานะการตรวจสอบ
- **RumourDetailView.java**: แสดงรายละเอียดเจาะลึกของข่าว ทั้งยอดรายงาน, คะแนนความร้อนแรง, และปุ่มดำเนินการ (Report/Verify)
- **SummaryView.java**: แสดงสรุปผลรวม โดยแบ่งเป็นตารางข่าว PANIC และตารางข่าวที่ตรวจสอบแล้ว (True/False)

### b) สรุป Routes/Actions และหน้าจอสำคัญ

1. **หน้า List View (หน้ารวมข่าว)**
    - **Sort/Filter**: ผู้ใช้เลือกเงื่อนไข --> Controller สั่ง Model กรองข้อมูล --> อัปเดตตารางทันที
    - **Open Detail**: เลือกข่าวในตาราง --> กดปุ่มเพื่อเปิดหน้า Detail
2. **หน้า Detail View (หน้ารายละเอียด)**
    - **Report**: กดรายงาน --> ตรวจสอบการซ้ำ --> บันทึก CSV (ถ้าครบเกณฑ์ที่กำหนดจะเปลี่ยนเป็น PANIC) --> ส่งสัญญาณไปหน้า Summary
    - **Verify**: (เฉพาะ Verifier) กด True/False --> ตรวจสอบเงื่อนไข --> บันทึกผล --> ส่งสัญญาณไปหน้า Summary
3. **หน้า Summary View (หน้าสรุปผล)**
    - **Auto-Refresh**: เมื่อมีการ Report/Verify สำเร็จ --> EventBus ส่งสัญญาณเตือน --> Summary อัปเดตตาราง PANIC และ Verified ทันที (ไม่ต้องกดรีเฟรชเอง)
