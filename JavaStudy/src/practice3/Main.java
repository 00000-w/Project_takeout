package practice3;

public class Main {
    public static void main(String[] args) {
        Student stu1 = new Student();
        Student stu2 = new Student();

        stu1.name = "张三";
        stu2.name = "李四";
        stu1.studentId = "01";
        stu2.studentId = "02";
        stu1.score = 90;
        stu2.score = 80;

        stu1.study();
        stu2.study();
        stu1.printInfo();
        stu2.printInfo();

        Student stu3 = new Student("李白", "03");
        stu3.setScore(55);
        Student stu4 = new Student("杜甫", "04");
        stu4.setScore(60);
        stu3.printInfo();
        stu4.printInfo();
    }
}
