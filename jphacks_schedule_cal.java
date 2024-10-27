import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Task> tasks2 = new ArrayList<>();
        Map<String, List<Event>> eventMap = new HashMap<>();

        // 任意のタスクを作成しリストに追加
        addTaskToList(tasks2, "Develop Feature A", 1, 2024, 10, 27, 2024, 10, 30, 3);
        addTaskToList(tasks2, "Write Documentation", 1, 2024, 10, 31, 2024, 11, 6, 2);
        addTaskToList(tasks2, "Deploy Application", 0, 2024, 11, 5, 2024, 11, 10, 1);
        addTaskToList(tasks2, "Feedback Session", 0, 2024, 11, 7, 2024, 11, 9, 1);
        addTaskToList(tasks2, "Prepare Release Notes", 1, 2024, 11, 6, 2024, 11, 10, 2);
        addTaskToList(tasks2, "Plan Next Sprint", 2, 2024, 11, 11, 2024, 11, 13, 2);

        // イベントを追加 (10月27日、10月31日、11月1日、11月5日は作業不可)
        addEventToMap(eventMap, "2024/10/27", "Team Meeting", 2, "10:00 AM - 11:00 AM");
        addEventToMap(eventMap, "2024/10/31", "Halloween Party", 2, "6:00 PM - 9:00 PM");
        addEventToMap(eventMap, "2024/11/5", "Deployment Review", 2, "3:00 PM - 4:00 PM");
        addEventToMap(eventMap, "2024/11/10", "Retrospective Meeting", 2, "4:00 PM - 5:00 PM");

        // 作成したタスクを表示
        displayTasks(tasks2);
        
        displayAllEvents(eventMap);

        // タスクを締切が早い順にソート
        sortTasksByDeadline(tasks2);

        // スケジュール計算の開始日を設定
        ThatDay today2 = new ThatDay();
        today2.year = 2024;
        today2.month = 10;
        today2.date = 26;
        Map<String, String> scheduleMap = new HashMap<>(); // 日付とタスク名を格納するマップ
        scheduleMap = calschedule(today2, tasks2.toArray(new Task[0]), eventMap);//日付がキー、値がタスク名のマップ
        
        for (String key : scheduleMap.keySet()) {
        System.out.println(key + ":" + scheduleMap.get(key));
    }
        
        
    }

    public static void displayTasks(List<Task> tasks) {
        System.out.println("作成されたタスク:");
        for (Task task : tasks) {
            System.out.println("タスク名: " + task.taskname + ", マージン: " + task.margin + ", 開始日: " + task.startday.year + "/" + task.startday.month + "/" + task.startday.date + ", 締切日: " + task.endday.year + "/" + task.endday.month + "/" + task.endday.date + ", 必要日数: " + task.reqday);
        }
        System.out.println(); // 改行
    }
    
    public static void displayAllEvents(Map<String, List<Event>> eventMap) {
    System.out.println("全てのイベント一覧:");
    
    for (String dateKey : eventMap.keySet()) {
        List<Event> events = eventMap.get(dateKey);
        System.out.println("日付: " + dateKey);
        
        for (Event event : events) {
            System.out.println("  イベント名: " + event.eventname +"  メモ: " + event.memo);
        }
    }
}

    public static void addTaskToList(List<Task> taskList, String taskname, int margin, int startYear, int startMonth, int startDate, int endYear, int endMonth, int endDate, int reqday) {
        Task newTask = new Task();
        newTask.taskname = taskname;
        newTask.margin = margin;
        newTask.reqday = reqday;
        newTask.startday.year = startYear;
        newTask.startday.month = startMonth;
        newTask.startday.date = startDate;
        newTask.endday.year = endYear;
        newTask.endday.month = endMonth;
        newTask.endday.date = endDate;
        taskList.add(newTask);
    }

    public static void addEventToMap(Map<String, List<Event>> eventMap, String date, String eventname, int degree, String memo) {
        Event newEvent = new Event();
        newEvent.eventname = eventname;
        newEvent.degree = degree;
        newEvent.memo = memo;
        eventMap.computeIfAbsent(date, k -> new ArrayList<>()).add(newEvent);
    }

    public static int displayEventsOnDate(Map<String, List<Event>> eventMap, String date) {
        List<Event> eventsOnDate = eventMap.get(date);
        int totalDegree = 0;
        System.out.println("-----------------------------------------------------------------------------------------------");
        if (eventsOnDate != null) {
            System.out.println(date + "のイベント:");
            for (Event event : eventsOnDate) {
                System.out.println("イベント名: " + event.eventname + ", 度合い: " + event.degree + ", メモ: " + event.memo);
                totalDegree += event.degree;
            }
        } else {
            System.out.println(date + "にイベントはありません。");
        }
        //System.out.println("度合いの合計: " + totalDegree);
        return totalDegree;
    }

    public static Map<String, String> calschedule(ThatDay firstday, Task[] tasks, Map<String, List<Event>> eventMap) {
        Calendar calendar = Calendar.getInstance();
        Map<String, String> scheduleMap = new HashMap<>(); // 日付とタスク名を格納するマップ
        int unavailableDays = 0;

        for (Task task : tasks) {
            int reqday = task.reqday;

            // タスクの開始日を設定
            calendar.set(task.startday.year, task.startday.month - 1, task.startday.date);

            // スケジュール計算
            while (reqday > 0) {
                String dateKey = String.format("%d/%02d/%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                int totalDegree = displayEventsOnDate(eventMap, dateKey);

                if (totalDegree <= 1) {
                    System.out.println("スケジュール登録: " + task.taskname + " on " + dateKey +"\n");
                    //ここにViewに値を送るメソッドを作ればよさそう
                    scheduleMap.put(dateKey, task.taskname+" "+reqday);
                    reqday--;
                } else {
                    System.out.println("作業不可: " + dateKey + " はイベントがあり、時間が確保できないため作業不可です。\n");
                    unavailableDays++;
                }

                // 次の日に移動
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return scheduleMap;
    }

    public static void sortTasksByDeadline(List<Task> tasks) {
        tasks.sort(Comparator.comparingInt(task -> task.endday.year * 10000 + task.endday.month * 100 + task.endday.date));
    }

    public static int dateDiff(String dateFromStrig, String dateToString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dateTo = null;
        Date dateFrom = null;
        try {
            dateFrom = sdf.parse(dateFromStrig);
            dateTo = sdf.parse(dateToString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        long dateTimeTo = dateTo.getTime();
        long dateTimeFrom = dateFrom.getTime();
        long dayDiff = (dateTimeTo - dateTimeFrom) / (1000 * 60 * 60 * 24);
        return (int) dayDiff;
    }

    public static class ThatDay {
        int year;
        int month;
        int date;
    }

    public static class ScheDay {
        String taskname;
        boolean margin;
        ThatDay day = new ThatDay();
    }

    public static class Task {
        String taskname;
        ThatDay startday = new ThatDay();
        ThatDay endday = new ThatDay();
        int margin;
        int reqday;

        public int getendyear() {
            return endday.year;
        }

        public int getendmonth() {
            return endday.month;
        }

        public int getenddate() {
            return endday.date;
        }
    }

    public static class Event {
        String eventname;
        int degree;
        String memo;
    }
}
