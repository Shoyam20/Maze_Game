import java.util.Scanner;

public class control {
    public static void main(String[] args) throws Exception{
        
        Scanner sc =new Scanner(System.in);
        Main1 m1=new Main1();
        Main2 m2=new Main2();
        Leaderboard lb = new Leaderboard();
        

        int choose;
        do{
            System.out.println("\n1. Level mode \n2. Hurdle mode \n3. Leaderboard \n0. Exit.... \nEnter");
            choose=sc.nextInt();

            switch(choose)
            {
                case 1:
                    System.out.println(("\n---level mode---\n"));
                    m1.func();
                    break;
                case 2:
                    System.out.println(("\n---Hurdle mode---\n"));
                    m2.func();
                    break;
                case 3:
                    lb.viewFromMenu();
                    break;
                default:
                    if(choose==0)
                        System.out.println("Exiting....");
                    else
                        System.out.println("Invalid choose");
            }
        }while(choose!=0);



    }
}
