package practice3;

public class Student {
    String name;
    String studentId;
    int score;

    //构造方法
    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    public Student() {}

    void setScore(int score) {
        this.score = score;
    }

    void study() {
        System.out.println(name + "正在学习java");
    }

    void printInfo() {
        System.out.println("姓名" + name);
        System.out.println("学号" + studentId);
        System.out.println("成绩" + score);
    }
}
