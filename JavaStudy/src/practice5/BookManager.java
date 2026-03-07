package practice5;

import java.util.ArrayList;
import java.util.Scanner;

public class BookManager {
    public static void main(String[] args) {
        ArrayList<Book> books = new ArrayList<>();
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println("\n====图书管理系统====");
            System.out.println("1.添加图书");
            System.out.println("2.退出系统");
            System.out.println("请选择：");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("请输入图书编号：");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("请输入书名：");
                    String name = scanner.nextLine();
                    System.out.println("请输入作者：");
                    String author = scanner.nextLine();
                    System.out.println("请输入价格：");
                    double price = scanner.nextDouble();
                    books.add(new Book(id, name, author, price));
                    System.out.println("添加成功！");
                    break;
                case 2:
                    System.out.println("再见ヾ(•ω•`)o");
                    running = false;
                    break;
                default :
                    System.out.println("无效输入");
            }
        }
        scanner.close();
    }
}
