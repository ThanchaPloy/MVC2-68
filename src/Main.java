import controller.RumourListController;
import controller.SummaryController;
import model.*;
import view.RumourListView;
import view.SummaryView;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class Main {

    private static final String DATA_DIR = "data";
    private static final String USERS_CSV = DATA_DIR + "/users.csv";
    private static final String RUMOURS_CSV = DATA_DIR + "/rumours.csv";
    private static final String REPORTS_CSV = DATA_DIR + "/reports.csv";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ensureSeedData();

                // Model layer
                CsvRumourRepository rumourRepo = new CsvRumourRepository(RUMOURS_CSV);
                CsvReportRepository reportRepo = new CsvReportRepository(REPORTS_CSV);
                CsvUserRepository userRepo = new CsvUserRepository(USERS_CSV);
                RumourService service = new RumourService(rumourRepo, reportRepo, userRepo);

                // View
                RumourListView listView = new RumourListView();

                // Controller
                RumourListController listController = new RumourListController(listView, service);
                listController.init();

                // Open Summary window
                listView.getBtnOpenSummary().addActionListener(e -> {
                    SummaryView sv = new SummaryView();
                    SummaryController sc = new SummaryController(sv, service);
                    sc.init();
                    sv.setVisible(true);
                });

                listView.setVisible(true);

            } catch (Exception ex) {
                // write full stacktrace to data/last_error.txt for diagnosis, then show a short dialog
                try {
                    File dir = new File(DATA_DIR);
                    if (!dir.exists()) dir.mkdirs();
                    try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, "last_error.txt")), StandardCharsets.UTF_8))) {
                        ex.printStackTrace(pw);
                    }
                } catch (IOException ioe) {
                    // ignore
                }
                JOptionPane.showMessageDialog(null, "Failed to start program: " + ex.getMessage(),
                        "Fatal", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    
    private static void ensureSeedData() throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

    // users.csv (>= 10 users)
        writeIfMissing(USERS_CSV,
                "userId,name,role\n" +
                        "U001,Anan,GENERAL\n" +
                        "U002,Bow,GENERAL\n" +
                        "U003,Chai,GENERAL\n" +
                        "U004,Dao,GENERAL\n" +
                        "U005,Eak,GENERAL\n" +
                        "U006,Fah,GENERAL\n" +
                        "U007,Golf,GENERAL\n" +
                        "U008,Hana,GENERAL\n" +
                        "U009,Ink,VERIFIER\n" +
                        "U010,Jack,VERIFIER\n"
        );

    // rumours.csv (>= 8 rumours, id is 8 digits and should not start with 0)
        writeIfMissing(RUMOURS_CSV,
                "rumourId,topic,source,createdDate,credibilityScore,status,verifiedResult\n" +
                    "12345678,เครื่องดื่มยี่ห้อดังปนเปื้อน,Facebook,2026-02-01,35,NORMAL,UNVERIFIED\n" +
                    "23456789,น้ำประปาจะหยุดจ่ายทั้งเมือง,TikTok,2026-02-02,40,NORMAL,UNVERIFIED\n" +
                    "34567891,มีแผ่นดินไหวใหญ่คืนนี้,Line,2026-02-03,20,PANIC,TRUE_INFO\n" +
                    "45678912,ไฟดับยาว 3 วันแน่นอน,Twitter,2026-02-03,50,NORMAL,UNVERIFIED\n" +
                    "56789123,วัคซีนล็อตใหม่อันตราย,Facebook,2026-02-04,25,NORMAL,UNVERIFIED\n" +
                    "67891234,ธนาคารจะล่มพรุ่งนี้เช้า,YouTube,2026-02-04,30,PANIC,UNVERIFIED\n" +
                    "78912345,พบสัตว์มีพิษหลุดในสวนสาธารณะ,NewsBlog,2026-02-05,55,NORMAL,UNVERIFIED\n" +
                    "89123456,โรงเรียนจะปิดกะทันหัน,Line,2026-02-05,45,NORMAL,UNVERIFIED\n" +
                    "91234567,อาหารเสริมยี่ห้อดังมีสารอันตราย,Instagram,2026-02-06,15,NORMAL,UNVERIFIED\n"
        );

    // reports.csv
        writeIfMissing(REPORTS_CSV,
                "reporterUserId,rumourId,reportDate,reportType\n" +
                        "U001,34567891,2026-02-03,FALSE_INFO\n" +
                        "U002,34567891,2026-02-03,INCITEMENT\n" +
                        "U003,34567891,2026-02-03,DISTORTION\n" +
                        "U004,34567891,2026-02-03,FALSE_INFO\n" +
                        "\n" +
                        "U001,67891234,2026-02-04,INCITEMENT\n" +
                        "U002,67891234,2026-02-04,INCITEMENT\n" +
                        "U003,67891234,2026-02-04,FALSE_INFO\n" +
                        "U005,67891234,2026-02-04,DISTORTION\n" +
                        "\n" +
                        "U006,12345678,2026-02-05,DISTORTION\n" +
                        "U007,12345678,2026-02-05,FALSE_INFO\n"
        );
    }

    private static void writeIfMissing(String path, String content) throws IOException {
        File f = new File(path);
        if (f.exists()) return;

        try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            w.write(content);
        }
    }
}
