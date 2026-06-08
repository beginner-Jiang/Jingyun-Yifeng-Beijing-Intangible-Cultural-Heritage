package org.example.beijing.controller;

import org.example.beijing.service.OnlineUserService;
import org.example.beijing.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/manage")
public class AdminController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OnlineUserService onlineUserService;

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${upload.image-path:./uploads/}")
    private String uploadPath;

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // 登录与退出
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/auth/login")
    public String adminLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("adminLogged", true);
            return "redirect:/manage/system-info";
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/manage/login";
    }

    // 管理页面（均需登录检查）
    @GetMapping({"", "/"})
    public String index(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "redirect:/manage/system-info";
    }

    @GetMapping("/system-info")
    public String systemInfo(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageSystemInfo";
    }

    @GetMapping("/users")
    public String users(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageUsers";
    }

    @GetMapping("/activities")
    public String activities(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageActivities";
    }

    @GetMapping("/heritages")
    public String heritages(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageHeritages";
    }

    @GetMapping("/recruits")
    public String recruits(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageRecruits";
    }

    @GetMapping("/comments")
    public String comments(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageComments";
    }

    @GetMapping("/artworks")
    public String artworks(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) return "redirect:/manage/login";
        return "manageArtworks";
    }

    // API 接口 

    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT (SELECT COUNT(*) FROM user) AS totalUsers, " +
                "(SELECT COUNT(*) FROM heritage_item) AS totalItems, " +
                "(SELECT COUNT(*) FROM heritage_site) AS totalSites, " +
                "(SELECT COUNT(*) FROM activity) AS totalActivities, " +
                "(SELECT COUNT(*) FROM collection) AS totalCollections, " +
                "(SELECT COUNT(*) FROM recruit_post) AS totalRecruits, " +
                "(SELECT COUNT(*) FROM comment) AS totalComments";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                stats.put("totalUsers", rs.getInt("totalUsers"));
                stats.put("totalItems", rs.getInt("totalItems"));
                stats.put("totalSites", rs.getInt("totalSites"));
                stats.put("totalActivities", rs.getInt("totalActivities"));
                stats.put("totalCollections", rs.getInt("totalCollections"));
                stats.put("totalRecruits", rs.getInt("totalRecruits"));
                stats.put("totalComments", rs.getInt("totalComments"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Map.of("code", 200, "data", stats);
    }

    @GetMapping("/api/online-count")
    @ResponseBody
    public Map<String, Object> getOnlineCount() {
        int count = onlineUserService.getOnlineCount();
        return Map.of("code", 200, "data", count);
    }

    @GetMapping("/api/system-info")
    @ResponseBody
    public Map<String, Object> getSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        String osNameRaw = osBean.getName();
        String osArch = osBean.getArch();
        String osVersion = System.getProperty("os.version");

        String osDisplayName = osNameRaw;
        if (osNameRaw.toLowerCase().contains("windows")) {
            String archSuffix = osArch.toLowerCase().contains("64") ? "64位" : "32位";
            if (osVersion != null && osVersion.startsWith("10.0")) {
                String buildNumber = osVersion.substring(4);
                try {
                    int build = Integer.parseInt(buildNumber);
                    if (build >= 22000) osDisplayName = "Windows 11 " + archSuffix;
                    else if (build >= 10240) osDisplayName = "Windows 10 " + archSuffix;
                    else osDisplayName = "Windows " + archSuffix;
                } catch (NumberFormatException e) { osDisplayName = "Windows " + archSuffix; }
            } else { osDisplayName = "Windows " + archSuffix; }
        }

        String serverTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> info = new HashMap<>();
        info.put("serverTime", serverTime);
        info.put("osName", osDisplayName);
        info.put("osArch", osArch);
        info.put("osVersion", osVersion);
        info.put("availableProcessors", osBean.getAvailableProcessors());
        info.put("systemLoadAverage", osBean.getSystemLoadAverage());

        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        info.put("totalMemoryMB", totalMemory);
        info.put("freeMemoryMB", freeMemory);
        info.put("usedMemoryMB", totalMemory - freeMemory);
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("userDir", System.getProperty("user.dir"));

        return Map.of("code", 200, "data", info);
    }

    // 用户管理 API 
    @GetMapping("/api/users")
    @ResponseBody
    public Map<String, Object> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        List<Map<String, Object>> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, username, email, phone, age, role, created_at, avatar FROM user WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (username LIKE ? OR email LIKE ? OR phone LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (minAge != null) {
            sql.append(" AND age >= ?");
            params.add(minAge);
        }
        if (maxAge != null) {
            sql.append(" AND age <= ?");
            params.add(maxAge);
        }
        sql.append(" ORDER BY id DESC");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> u = new HashMap<>();
                Long uid = rs.getLong("id");
                u.put("id", uid);
                u.put("username", rs.getString("username"));
                u.put("email", rs.getString("email"));
                u.put("phone", rs.getString("phone"));
                u.put("age", rs.getInt("age"));
                u.put("role", rs.getString("role"));
                u.put("avatar", rs.getString("avatar"));
                Timestamp ts = rs.getTimestamp("created_at");
                u.put("created_at", ts != null ? ts.toString() : "");
                u.put("online", onlineUserService.isOnline(uid));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Map.of("code", 200, "data", users);
    }

    @PostMapping("/api/users/add")
    @ResponseBody
    public Map<String, String> addUser(@RequestBody Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String email = (String) userData.get("email");
        String phone = (String) userData.get("phone");
        Integer age = (Integer) userData.get("age");
        String role = (String) userData.get("role");
        String password = (String) userData.get("password");
        if (username == null || username.trim().isEmpty()) return Map.of("msg", "用户名不能为空");
        if (password == null || password.length() < 6) return Map.of("msg", "密码至少6位");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO user (username, email, phone, age, role, password, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())")) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, age != null ? age : 0);
            ps.setString(5, role != null ? role : "user");
            ps.setString(6, password);
            ps.executeUpdate();
            return Map.of("msg", "添加成功");
        } catch (SQLException e) {
            e.printStackTrace();
            return Map.of("msg", "添加失败：" + e.getMessage());
        }
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public Map<String, String> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String email = (String) userData.get("email");
        String phone = (String) userData.get("phone");
        Integer age = (Integer) userData.get("age");
        String role = (String) userData.get("role");
        String avatar = (String) userData.get("avatar");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE user SET username=?, email=?, phone=?, age=?, role=?, avatar=? WHERE id=?")) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, age != null ? age : 0);
            ps.setString(5, role);
            ps.setString(6, avatar);
            ps.setLong(7, id);
            ps.executeUpdate();
            return Map.of("msg", "更新成功");
        } catch (SQLException e) {
            e.printStackTrace();
            return Map.of("msg", "更新失败：" + e.getMessage());
        }
    }

    @PostMapping("/api/users/delete")
    @ResponseBody
    public Map<String, String> deleteUser(@RequestParam Long userId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String[] tables = {"collection", "activity_signup", "recruit_application", "comment", "footprint", "login_log", "artwork"};
                for (String table : tables) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE user_id=?")) {
                        ps.setLong(1, userId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM recruit_post WHERE inheritor_id=?")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM activity WHERE inheritor_id=?")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE heritage_item SET inheritor_id=NULL WHERE inheritor_id=?")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM inheritor WHERE user_id=?")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM user WHERE id=?")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
                conn.commit();
                return Map.of("msg", "删除成功");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Map.of("msg", "删除失败：" + e.getMessage());
        }
    }

    @PostMapping("/api/impersonate")
    @ResponseBody
    public Map<String, String> impersonate(@RequestParam Long userId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT username, role FROM user WHERE id=?")) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                String token = jwtUtil.generateToken(userId, username, role);
                return Map.of("token", token, "username", username, "msg", "success");
            } else {
                return Map.of("msg", "用户不存在");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Map.of("msg", "模拟登录失败");
        }
    }

    // 活动管理 API
    @GetMapping("/api/activities")
    @ResponseBody
    public Map<String, Object> getActivities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, title, type, inheritor_id, status, image_url, description, start_time, end_time, max_participants, current_participants FROM activity WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like); params.add(like);
        }
        if (status != null) { sql.append(" AND status = ?"); params.add(status); }
        sql.append(" ORDER BY id DESC");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("title", rs.getString("title"));
                m.put("type", rs.getString("type"));
                m.put("inheritor_id", rs.getInt("inheritor_id"));
                m.put("status", rs.getInt("status"));
                m.put("image_url", rs.getString("image_url"));
                m.put("description", rs.getString("description"));
                m.put("start_time", rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toString() : null);
                m.put("end_time", rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toString() : null);
                m.put("max_participants", rs.getInt("max_participants"));
                m.put("current_participants", rs.getInt("current_participants"));
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Map.of("code", 200, "data", list);
    }

    @PostMapping("/api/activities/add")
    @ResponseBody
    public Map<String, String> addActivity(@RequestBody Map<String, Object> activityData) {
        String title = (String) activityData.get("title");
        String type = (String) activityData.get("type");
        String description = (String) activityData.get("description");
        String imageUrl = (String) activityData.get("image_url");
        String startTime = (String) activityData.get("start_time");
        String endTime = (String) activityData.get("end_time");
        Integer maxParticipants = (Integer) activityData.get("max_participants");
        Long inheritorId = activityData.get("inheritor_id") != null ? ((Number) activityData.get("inheritor_id")).longValue() : null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO activity (title, type, description, image_url, start_time, end_time, max_participants, inheritor_id, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, NOW())")) {
            ps.setString(1, title);
            ps.setString(2, type);
            ps.setString(3, description);
            ps.setString(4, imageUrl);
            ps.setString(5, startTime);
            ps.setString(6, endTime);
            ps.setInt(7, maxParticipants != null ? maxParticipants : 20);
            if (inheritorId != null) ps.setLong(8, inheritorId);
            else ps.setNull(8, java.sql.Types.BIGINT);
            ps.executeUpdate();
            return Map.of("msg", "添加成功");
        } catch (SQLException e) { e.printStackTrace(); return Map.of("msg", "添加失败：" + e.getMessage()); }
    }

    @PutMapping("/api/activities/{id}")
    @ResponseBody
    public Map<String, String> updateActivity(@PathVariable Long id, @RequestBody Map<String, Object> activityData) {
        String title = (String) activityData.get("title");
        String type = (String) activityData.get("type");
        String description = (String) activityData.get("description");
        String imageUrl = (String) activityData.get("image_url");
        String startTime = (String) activityData.get("start_time");
        String endTime = (String) activityData.get("end_time");
        Integer maxParticipants = (Integer) activityData.get("max_participants");
        Integer status = (Integer) activityData.get("status");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE activity SET title=?, type=?, description=?, image_url=?, start_time=?, end_time=?, max_participants=?, status=? WHERE id=?")) {
            ps.setString(1, title);
            ps.setString(2, type);
            ps.setString(3, description);
            ps.setString(4, imageUrl);
            ps.setString(5, startTime);
            ps.setString(6, endTime);
            ps.setInt(7, maxParticipants != null ? maxParticipants : 20);
            ps.setInt(8, status != null ? status : 1);
            ps.setLong(9, id);
            ps.executeUpdate();
            return Map.of("msg", "更新成功");
        } catch (SQLException e) { e.printStackTrace(); return Map.of("msg", "更新失败：" + e.getMessage()); }
    }

    @PostMapping("/api/activities/delete")
    @ResponseBody
    public Map<String, String> deleteActivity(@RequestParam Long activityId) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM activity_signup WHERE activity_id=?")) { ps.setLong(1, activityId); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM activity WHERE id=?")) { ps.setLong(1, activityId); ps.executeUpdate(); }
            return Map.of("msg", "删除成功");
        } catch (SQLException e) { return Map.of("msg", "删除失败：" + e.getMessage()); }
    }

    // 非遗项目管理 API 
    @GetMapping("/api/heritages")
    @ResponseBody
    public Map<String, Object> getHeritages(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, name, category, region, level, description, image_url, inheritor_id FROM heritage_item WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like); params.add(like);
        }
        if (category != null && !category.isEmpty()) { sql.append(" AND category = ?"); params.add(category); }
        if (level != null && !level.isEmpty()) { sql.append(" AND level = ?"); params.add(level); }
        sql.append(" ORDER BY id DESC");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                m.put("category", rs.getString("category"));
                m.put("region", rs.getString("region"));
                m.put("level", rs.getString("level"));
                m.put("description", rs.getString("description"));
                m.put("image_url", rs.getString("image_url"));
                m.put("inheritor_id", rs.getInt("inheritor_id"));
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Map.of("code", 200, "data", list);
    }

    @PostMapping("/api/heritages/add")
    @ResponseBody
    public Map<String, String> addHeritage(@RequestBody Map<String, Object> heritageData) {
        String name = (String) heritageData.get("name");
        String category = (String) heritageData.get("category");
        String region = (String) heritageData.get("region");
        String level = (String) heritageData.get("level");
        String description = (String) heritageData.get("description");
        String imageUrl = (String) heritageData.get("image_url");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO heritage_item (name, category, region, level, description, image_url) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, region);
            ps.setString(4, level);
            ps.setString(5, description);
            ps.setString(6, imageUrl);
            ps.executeUpdate();
            return Map.of("msg", "添加成功");
        } catch (SQLException e) { e.printStackTrace(); return Map.of("msg", "添加失败：" + e.getMessage()); }
    }

    @PutMapping("/api/heritages/{id}")
    @ResponseBody
    public Map<String, String> updateHeritage(@PathVariable Long id, @RequestBody Map<String, Object> heritageData) {
        String name = (String) heritageData.get("name");
        String category = (String) heritageData.get("category");
        String region = (String) heritageData.get("region");
        String level = (String) heritageData.get("level");
        String description = (String) heritageData.get("description");
        String imageUrl = (String) heritageData.get("image_url");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE heritage_item SET name=?, category=?, region=?, level=?, description=?, image_url=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, region);
            ps.setString(4, level);
            ps.setString(5, description);
            ps.setString(6, imageUrl);
            ps.setLong(7, id);
            ps.executeUpdate();
            return Map.of("msg", "更新成功");
        } catch (SQLException e) { e.printStackTrace(); return Map.of("msg", "更新失败：" + e.getMessage()); }
    }

    @PostMapping("/api/heritages/delete")
    @ResponseBody
    public Map<String, String> deleteHeritage(@RequestParam Long heritageId) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM collection WHERE target_type='item' AND target_id=?")) { ps.setLong(1, heritageId); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM heritage_item WHERE id=?")) { ps.setLong(1, heritageId); ps.executeUpdate(); }
            return Map.of("msg", "删除成功");
        } catch (SQLException e) { return Map.of("msg", "删除失败：" + e.getMessage()); }
    }

    // 招募管理 API 
    @GetMapping("/api/recruits")
    @ResponseBody
    public Map<String, Object> getRecruits(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, title, description, inheritor_name, status, max_applicants, applicant_count, created_at FROM recruit_post WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ? OR inheritor_name LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (status != null) { sql.append(" AND status = ?"); params.add(status); }
        sql.append(" ORDER BY id DESC");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("title", rs.getString("title"));
                m.put("description", rs.getString("description"));
                m.put("inheritor_name", rs.getString("inheritor_name"));
                m.put("status", rs.getInt("status"));
                m.put("max_applicants", rs.getInt("max_applicants"));
                m.put("applicant_count", rs.getInt("applicant_count"));
                Timestamp ts = rs.getTimestamp("created_at");
                m.put("created_at", ts != null ? ts.toString() : "");
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Map.of("code", 200, "data", list);
    }

    @PostMapping("/api/recruits/add")
    @ResponseBody
    public Map<String, String> addRecruit(@RequestBody Map<String, Object> recruitData) {
        String title = (String) recruitData.get("title");
        String description = (String) recruitData.get("description");
        String inheritorName = (String) recruitData.get("inheritor_name");
        Integer maxApplicants = (Integer) recruitData.get("max_applicants");
        Long inheritorId = recruitData.get("inheritor_id") != null ? ((Number) recruitData.get("inheritor_id")).longValue() : null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO recruit_post (title, description, inheritor_name, inheritor_id, max_applicants, status, created_at) VALUES (?, ?, ?, ?, ?, 0, NOW())")) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, inheritorName);
            if (inheritorId != null) ps.setLong(4, inheritorId);
            else ps.setNull(4, java.sql.Types.BIGINT);
            ps.setInt(5, maxApplicants != null ? maxApplicants : 10);
            ps.executeUpdate();
            return Map.of("msg", "添加成功");
        } catch (SQLException e) { e.printStackTrace(); return Map.of("msg", "添加失败：" + e.getMessage()); }
    }

    @PutMapping("/api/recruits/{id}")
    @ResponseBody
    public Map<String, String> updateRecruit(@PathVariable Long id, @RequestBody Map<String, Object> recruitData) {
        String title = (String) recruitData.get("title");
        String description = (String) recruitData.get("description");
        String inheritorName = (String) recruitData.get("inheritor_name");
        Integer maxApplicants = (Integer) recruitData.get("max_applicants");
        Integer status = (Integer) recruitData.get("status");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE recruit_post SET title=?, description=?, inheritor_name=?, max_applicants=?, status=? WHERE id=?")) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, inheritorName);
            ps.setInt(4, maxApplicants != null ? maxApplicants : 10);
            ps.setInt(5, status != null ? status : 0);
            ps.setLong(6, id);
            ps.executeUpdate();
            return Map.of("msg", "更新成功");
        } catch (SQLException e) { e.printStackTrace(); return Map.of("msg", "更新失败：" + e.getMessage()); }
    }

    @PostMapping("/api/recruits/delete")
    @ResponseBody
    public Map<String, String> deleteRecruit(@RequestParam Long recruitId) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM recruit_application WHERE recruit_id=?")) { ps.setLong(1, recruitId); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM recruit_post WHERE id=?")) { ps.setLong(1, recruitId); ps.executeUpdate(); }
            return Map.of("msg", "删除成功");
        } catch (SQLException e) { return Map.of("msg", "删除失败：" + e.getMessage()); }
    }

    // 评论管理 API 
    @GetMapping("/api/comments")
    @ResponseBody
    public Map<String, Object> getComments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT c.id, c.user_id, c.content, c.created_at, u.username FROM comment c LEFT JOIN user u ON c.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (c.content LIKE ? OR u.username LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like); params.add(like);
        }
        if (userId != null) { sql.append(" AND c.user_id = ?"); params.add(userId); }
        sql.append(" ORDER BY c.id DESC");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("user_id", rs.getInt("user_id"));
                m.put("username", rs.getString("username"));
                m.put("content", rs.getString("content"));
                Timestamp ts = rs.getTimestamp("created_at");
                m.put("created_at", ts != null ? ts.toString() : "");
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Map.of("code", 200, "data", list);
    }

    @PostMapping("/api/comments/delete")
    @ResponseBody
    public Map<String, String> deleteComment(@RequestParam Long commentId) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM comment WHERE id=?")) {
            ps.setLong(1, commentId);
            ps.executeUpdate();
            return Map.of("msg", "删除成功");
        } catch (SQLException e) { return Map.of("msg", "删除失败：" + e.getMessage()); }
    }

    // 作品管理 API 
    @GetMapping("/api/artworks")
    @ResponseBody
    public Map<String, Object> getArtworks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String inheritorName,
            @RequestParam(required = false) Long inheritorId) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT a.id, a.title, a.description, a.image_url, a.inheritor_id, a.created_at, " +
            "i.name as inheritor_name " +
            "FROM artwork a LEFT JOIN inheritor i ON a.inheritor_id = i.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (a.title LIKE ? OR a.description LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like); params.add(like);
        }
        if (inheritorName != null && !inheritorName.isEmpty()) {
            sql.append(" AND i.name LIKE ?");
            params.add("%" + inheritorName + "%");
        }
        if (inheritorId != null) {
            sql.append(" AND a.inheritor_id = ?");
            params.add(inheritorId);
        }
        sql.append(" ORDER BY a.id DESC");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("title", rs.getString("title"));
                m.put("description", rs.getString("description"));
                m.put("image_url", rs.getString("image_url"));
                m.put("inheritor_id", rs.getInt("inheritor_id"));
                m.put("inheritor_name", rs.getString("inheritor_name"));
                Timestamp ts = rs.getTimestamp("created_at");
                m.put("created_at", ts != null ? ts.toString() : "");
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Map.of("code", 200, "data", list);
    }

    @PostMapping("/api/artworks/add")
    @ResponseBody
    public Map<String, String> addArtwork(@RequestBody Map<String, Object> artworkData) {
        String title = (String) artworkData.get("title");
        String description = (String) artworkData.get("description");
        String imageUrl = (String) artworkData.get("image_url");
        Long inheritorId = artworkData.get("inheritor_id") != null ? ((Number) artworkData.get("inheritor_id")).longValue() : null;
        if (title == null || title.trim().isEmpty()) return Map.of("msg", "标题不能为空");
        if (imageUrl == null || imageUrl.isEmpty()) return Map.of("msg", "请上传作品图片");
        if (inheritorId == null) return Map.of("msg", "传承人ID不能为空");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO artwork (title, description, image_url, inheritor_id, created_at) VALUES (?, ?, ?, ?, NOW())")) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, imageUrl);
            ps.setLong(4, inheritorId);
            ps.executeUpdate();
            return Map.of("msg", "添加成功");
        } catch (SQLException e) {
            e.printStackTrace();
            return Map.of("msg", "添加失败：" + e.getMessage());
        }
    }

    @PutMapping("/api/artworks/{id}")
    @ResponseBody
    public Map<String, String> updateArtwork(@PathVariable Long id, @RequestBody Map<String, Object> artworkData) {
        String title = (String) artworkData.get("title");
        String description = (String) artworkData.get("description");
        String imageUrl = (String) artworkData.get("image_url");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE artwork SET title=?, description=?, image_url=? WHERE id=?")) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, imageUrl);
            ps.setLong(4, id);
            ps.executeUpdate();
            return Map.of("msg", "更新成功");
        } catch (SQLException e) {
            e.printStackTrace();
            return Map.of("msg", "更新失败：" + e.getMessage());
        }
    }

    @PostMapping("/api/artworks/delete")
    @ResponseBody
    public Map<String, String> deleteArtwork(@RequestParam Long artworkId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM artwork WHERE id=?")) {
            ps.setLong(1, artworkId);
            ps.executeUpdate();
            return Map.of("msg", "删除成功");
        } catch (SQLException e) {
            return Map.of("msg", "删除失败：" + e.getMessage());
        }
    }

    // 图片上传 API
    @PostMapping("/api/upload")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam(value = "type", required = false) String type) {
        if (file.isEmpty()) return Map.of("msg", "文件为空");
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            String subDir = type != null ? type + "/" : "general/";
            File uploadDir = new File(uploadPath + subDir);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            String fileUrl = "/uploads/" + subDir + filename;
            return Map.of("url", fileUrl, "msg", "上传成功");
        } catch (IOException e) { e.printStackTrace(); return Map.of("msg", "上传失败：" + e.getMessage()); }
    }
}