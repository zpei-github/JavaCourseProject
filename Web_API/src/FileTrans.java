import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileTrans {
    public static final Integer LIMIT=400000;
    public static void main(String[] args) {
        {
            String csvFile_1 = "res/mashup.csv";
            String outputCsvFile_1 = "output/output.csv";
            String line;
            // 正则表达式，引号内部的逗号不分割
            String csvSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

            int limit = 0;
            try (BufferedReader br = new BufferedReader(new java.io.FileReader(csvFile_1));
                 PrintWriter pw = new PrintWriter(new FileWriter(outputCsvFile_1))) {
                while (((line = br.readLine()) != null) & limit<LIMIT) {
                    // 读取前三项进行操作
                    String[] items = line.split(csvSplitBy);
                    String[] firstThreeItems = new String[3];
                    System.arraycopy(items, 0, firstThreeItems, 0, Math.min(items.length, 3));
                    // 判断数字行才操作
                    if (isNumeric(firstThreeItems[0])) {
                        // 对每一项进行处理
                        for (int i = 1; i<3; i++) {
                            String item = firstThreeItems[i];
                            if (i == 1) {
                                assert item != null : "item should not be null";
                                System.out.print("["+item+"]\t");
                                pw.print(item);
                                pw.print(",");
                            }
                            if (i == 2) {
                                assert item != null : "item should not be null";
                                // 去除引号
                                item = item.replaceAll("^\"|\"$", "");
                                // 分割第二项子项
                                String[] subItems = item.split(",");
                                for (String subItem : subItems) {
                                    System.out.print(subItem+", ");
                                    pw.print(subItem);
                                    pw.print(",");
                                }
                            }
                        }
                        System.out.println();
                        pw.println(); // 换行
                    }
                    limit++;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        {
            String csvFile_2 = "res/api.csv";
            String outputCsvFile_2 = "output/output_2.csv";
            String line;
            // 正则表达式，引号内部的逗号不分割
            String csvSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

            int limit = 0;
            try (BufferedReader br = new BufferedReader(new java.io.FileReader(csvFile_2));
                 PrintWriter pw = new PrintWriter(new FileWriter(outputCsvFile_2))) {
                while (((line = br.readLine()) != null) & limit < LIMIT) {
                    // 读取前三项进行操作
                    String[] items = line.split(csvSplitBy);
                    String[] firstFiveItems = new String[5];
                    System.arraycopy(items, 0, firstFiveItems, 0, Math.min(items.length, 5));
                    // 判断数字行才操作
                    if (isNumeric(firstFiveItems[0])) {
                        // 对每一项进行处理
                        for (int i = 1; i < 5; i++) {
                            String item = firstFiveItems[i];
                            if (i == 1) {
                                assert item != null : "item should not be null";
                                System.out.print("[" + item + "]\t");
                                pw.print(item);
                                pw.print(",");
                            }
                            if ((i == 2) || (i == 4)) {
                                assert item != null : "item should not be null";
                                // 去除引号
                                try {
                                    item = item.replaceAll("^\"|\"$", "");
                                    // 分割第二项子项
                                    String[] subItems = item.split(",");
                                    for (String subItem : subItems) {
                                        System.out.print(subItem + ", ");
                                        pw.print(subItem);
                                        pw.print(",");
                                    }
                                } catch(NullPointerException e) {
                                    // 处理 item 为 null 的情况
                                    e.printStackTrace(); // 或者使用其他适当的异常处理逻辑
                                }
                            }
                        }
                        System.out.println();
                        pw.println(); // 换行
                    }
                    limit++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        {
            String csvFile_3 = "res/mashup.csv";
            String outputCsvFile_3 = "output/output_3.csv";
            String line;
            // 正则表达式，引号内部的逗号不分割
            String csvSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

            int limit = 0;
            try (BufferedReader br = new BufferedReader(new java.io.FileReader(csvFile_3));
                 PrintWriter pw = new PrintWriter(new FileWriter(outputCsvFile_3))) {
                while (((line = br.readLine()) != null) & limit < LIMIT) {
                    // 读取前三项进行操作
                    String[] items = line.split(csvSplitBy);
                    String[] firstFourItems = new String[4];
                    System.arraycopy(items, 0, firstFourItems, 0, Math.min(items.length, 4));
                    // 判断数字行才操作
                    if (isNumeric(firstFourItems[0])) {
                        // 对每一项进行处理
                        for (int i = 2; i < 4; i++) {
                            String item = firstFourItems[i];
                            if (i == 2) {
                                assert item != null : "item should not be null";
                                // 去除引号
                                item = item.replaceAll("^\"|\"$", "");
                                // 分割第二项子项
                                String[] subItems = item.split(",");
                                for (String subItem : subItems) {
                                    System.out.print(subItem + ", ");
                                    pw.print(subItem);
                                    pw.print(",");
                                }
                            }
                            if (i == 3) {
                                assert item != null : "item should not be null";
                                // 去除引号
                                item = item.replaceAll("^\"|\"$", "");
                                // 分割第三项子项
                                String[] subItems = item.split(",");
                                for (String subItem : subItems) {
                                    System.out.print(subItem + ", ");
                                    pw.print(subItem);
                                    pw.print(",");
                                }
                            }
                            System.out.println();
                            pw.println(); // 换行
                        }
                    }
                    limit++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static boolean isNumeric(String str) {
        try {
            int num = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
