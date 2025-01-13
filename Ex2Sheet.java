package assignments.ex2;

import java.io.*;

public class Ex2Sheet implements Sheet {
    private Cell[][] cells;

    public Ex2Sheet(int w, int h) {
        cells = new SCell[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                cells[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && y >= 0 && x < width() && y < height();
    }

    @Override
    public int width() {
        return cells.length;
    }

    @Override
    public int height() {
        return cells[0].length;
    }

    @Override
    public void set(int x, int y, String value) {
        if (!isIn(x, y)) return;
        if (value == null) value = "";

        if (SCell.isNumber(value)) {
            cells[x][y] = new SCell(value);
        } else if (value.startsWith("=")) {
            if (isProbablyValidFormula(value)) {
                cells[x][y] = new SCell(value);
            } else {
                cells[x][y] = new SCell(Ex2Utils.ERR_FORM);
            }
        } else {
            cells[x][y] = new SCell(value);
        }
    }

    @Override
    public Cell get(int x, int y) {
        if (isIn(x, y)) {
            return cells[x][y];
        }
        return null;
    }

    @Override
    public Cell get(String entry) {
        Index2D idx = parseIndex(entry);
        if (idx != null && isIn(idx.getX(), idx.getY())) {
            return cells[idx.getX()][idx.getY()];
        }
        return null;
    }

    private Index2D parseIndex(String entry) {
        if (entry == null || entry.isEmpty()) return null;

        String colPart = entry.replaceAll("[0-9]", "");
        String rowPart = entry.replaceAll("[A-Z]", "");
        if (colPart.isEmpty() || rowPart.isEmpty()) return null;

        int x = 0;
        for (char c : colPart.toCharArray()) {
            x = x * 26 + (c - 'A');
        }

        try {
            int y = Integer.parseInt(rowPart) - 1;
            return new CellEntry(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isProbablyValidFormula(String val) {
        if (val.length() < 2) return false;
        try {
            SCell.computeForm(val);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String value(int x, int y) {
        boolean[][] visited = new boolean[width()][height()];
        return evaluateCell(x, y, visited);
    }

    @Override
    public String eval(int x, int y) {
        return value(x, y);
    }

    private String evaluateCell(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        if (visited[x][y]) return Ex2Utils.ERR_CYCLE;

        visited[x][y] = true;
        Cell c = cells[x][y];
        if (c == null || c.getData() == null) return Ex2Utils.EMPTY_CELL;

        String data = c.getData();
        if (SCell.isNumber(data)) {
            return data;
        }
        if (data.startsWith("=")) {
            return evaluateFormula(data, visited);
        }
        return data;
    }

    private String evaluateFormula(String data, boolean[][] visited) {
        try {
            double val = SCell.computeForm(data);
            if (Double.isInfinite(val) || Double.isNaN(val)) {
                return Ex2Utils.ERR_FORM; // Handle division by zero and other invalid operations
            }
            return String.valueOf(val);
        } catch (IllegalArgumentException e) {
            return Ex2Utils.ERR_FORM;
        }
    }

    @Override
    public void eval() {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                value(i, j);
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                depths[i][j] = computeDepth(i, j, new boolean[width()][height()]);
            }
        }
        return depths;
    }

    private int computeDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y) || visited[x][y]) return -1; // Cycle detected
        visited[x][y] = true;

        Cell c = cells[x][y];
        if (c == null || c.getData() == null) return 0;

        String data = c.getData();
        if (SCell.isNumber(data) || !data.startsWith("=")) return 0;

        int maxDepth = 0;
        String[] tokens = data.substring(1).split("[+\\-*/()]");
        for (String token : tokens) {
            if (isCellReference(token)) {
                Index2D idx = parseIndex(token);
                if (idx != null) {
                    int depth = computeDepth(idx.getX(), idx.getY(), visited);
                    if (depth == -1) return -1; // Propagate cycle
                    maxDepth = Math.max(maxDepth, depth);
                }
            }
        }
        return maxDepth + 1;
    }

    private boolean isCellReference(String token) {
        if (token == null || token.isEmpty()) return false;
        return token.matches("[A-Z]+[0-9]+");
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    writer.write(cells[i][j].getData());
                    if (j < height() - 1) writer.write(",");
                }
                writer.newLine();
            }
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < width()) {
                String[] values = line.split(",");
                for (int j = 0; j < values.length && j < height(); j++) {
                    set(i, j, values[j]);
                }
                i++;
            }
        }
    }
}