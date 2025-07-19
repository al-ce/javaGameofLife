import plane.Plane;

public class Main {
    public static void main(String[] args) {

        Plane p = new Plane(10, 10);
        try {
            p.ToggleCell(0, 20);
        } catch (Plane.CellIndexOutOfBoundException e) {
            System.out.println(e);
        }
        p.PrintCells();

    }
}
