import plane.Plane;

public class Main {

    static void sep() {
        System.out.print("\n---------\n");
    }

    public static void main(String[] args) {

        // Initialize plane
        Plane p = new Plane(10, 10);

        // Set some cells to true
        p.ToggleCell(0, 2);
        p.ToggleCell(0, 3);
        p.ToggleCell(1, 3);
        p.ToggleCell(1, 4);
        p.PrintCells();

        sep();
        p.Evolve();
        p.PrintCells();

        sep();
        p.Evolve();
        p.PrintCells();
    }
}
