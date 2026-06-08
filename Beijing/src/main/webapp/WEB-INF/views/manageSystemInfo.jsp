<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>京韵遗风·管理后台</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="stylesheet" href="/css/common.css">
  <link rel="icon" href="/images/favicon.ico" type="image/x-icon">
  <style>
    .nav-link-jingyun {
      white-space: nowrap !important;
    }

    .navbar-jingyun .d-flex {
      flex-wrap: nowrap !important;
      overflow-x: auto !important;
      white-space: nowrap !important;
    }

    .nav-link-jingyun {
      padding: 0.5rem 1rem !important;
      font-size: 0.95rem !important;
    }

    .stat-card-enhanced {
      background: rgba(247, 237, 225, 0.9);
      border-radius: 1.5rem;
      padding: 1.2rem;
      text-align: center;
      transition: transform 0.2s, box-shadow 0.2s;
      height: 100%;
      border: 1px solid #d6bc9a;
    }

    .stat-card-enhanced:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    }

    .stat-icon {
      font-size: 2.5rem;
      color: #b6844e;
      margin-bottom: 0.5rem;
    }

    .stat-number {
      font-size: 2rem;
      font-weight: bold;
      color: #2e1e12;
    }

    .stat-label {
      color: #6b4f33;
      margin-top: 0.3rem;
    }

    .info-card {
      background: rgba(247, 237, 225, 0.85);
      border-radius: 2rem;
      padding: 2rem;
    }

    body.theme-dark .stat-card-enhanced,
    body.theme-dark .info-card {
      background: #2d2d2d;
    }

    body.theme-dark .stat-number,
    body.theme-dark .stat-label {
      color: #e0e0e0;
    }

    body.theme-soft .stat-card-enhanced,
    body.theme-soft .info-card {
      background: #fff8e7;
    }

    body.theme-soft .stat-number,
    body.theme-soft .stat-label {
      color: #5f4b3a;
    }

    .info-item {
      margin: 1rem 0;
      display: flex;
      border-bottom: 1px dashed #c9af8b;
      padding-bottom: 0.5rem;
    }

    .info-label {
      font-weight: bold;
      width: 150px;
      color: #815f3f;
    }

    .info-value {
      color: #2e1e12;
      font-family: monospace;
    }

    body.theme-dark .info-label,
    body.theme-dark .info-value {
      color: #e0e0e0;
    }

    body.theme-soft .info-label,
    body.theme-soft .info-value {
      color: #5f4b3a;
    }

    .loading-spinner {
      display: inline-block;
      width: 2rem;
      height: 2rem;
      border: 0.25rem solid #e9ecef;
      border-top-color: #b6844e;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }

    .stats-loading,
    .info-loading {
      text-align: center;
      padding: 2rem;
      color: #b6844e;
    }

    .logout-btn {
      position: fixed;
      bottom: 2rem;
      right: 2rem;
      z-index: 1000;
      width: 48px;
      height: 48px;
      border-radius: 50%;
      background: #dc3545;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
      transition: 0.2s;
      border: none;
      font-size: 1.2rem;
    }

    .logout-btn:hover {
      background: #c82333;
      transform: scale(1.1);
    }

    body.theme-dark .logout-btn {
      background: #a00;
    }

    body.theme-soft .logout-btn {
      background: #c2a27e;
    }

    body.theme-soft .logout-btn:hover {
      background: #d2b28e;
    }

    .system-intro-modal .intro-logo img {
      max-width: 180px !important;
      height: auto !important;
      width: auto !important;
      object-fit: contain !important;
    }

    .system-intro-modal .modal-footer {
      justify-content: center !important;
    }

    .system-intro-modal .modal-body {
      text-align: center;
    }

    .system-intro-modal ul {
      text-align: left;
      display: inline-block;
    }
  </style>
</head>
<body>
<div class="container-jingyun">
  <nav class="navbar-jingyun d-flex justify-content-between align-items-center">
    <div class="navbar-brand" style="cursor: pointer;"><i class="fas fa-landmark"></i> 京韵遗风·管理后台</div>
    <div class="d-flex">
      <a href="/manage/system-info" class="nav-link-jingyun active"><i class="fas fa-server"></i> 系统状态</a>
      <a href="/manage/users" class="nav-link-jingyun"><i class="fas fa-users"></i> 用户管理</a>
      <a href="/manage/activities" class="nav-link-jingyun"><i class="fas fa-calendar-alt"></i> 活动管理</a>
      <a href="/manage/artworks" class="nav-link-jingyun"><i class="fas fa-palette"></i> 作品管理</a>
      <a href="/manage/heritages" class="nav-link-jingyun"><i class="fas fa-landmark"></i> 非遗项目</a>
      <a href="/manage/recruits" class="nav-link-jingyun"><i class="fas fa-handshake"></i> 招募管理</a>
      <a href="/manage/comments" class="nav-link-jingyun"><i class="fas fa-comment"></i> 评论管理</a>
    </div>
  </nav>

  <div class="page-header">
    <h1>系·统·状·态</h1>
    <p>服务器实时信息 + 平台数据总览</p>
  </div>

  <div class="row g-4 mb-5" id="statsContainer">
    <div class="col-12 text-center stats-loading">
      <div class="loading-spinner"></div>
      <p class="mt-2">加载统计数据中...</p>
    </div>
  </div>

  <div class="row">
    <div class="col-lg-12">
      <div class="info-card">
        <h4><i class="fas fa-server"></i> 服务器信息</h4>
        <div id="serverInfoDetails">
          <div class="text-center info-loading">
            <div class="loading-spinner"></div>
            <p class="mt-2">加载服务器信息中...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="footer-note mt-4"><i class="fas fa-copyright"></i> 京韵遗风 · 数字博物馆  |  管理后台</div>
</div>

<!-- 独立的管理后台说明弹窗 -->
<div class="modal fade system-intro-modal" id="adminAboutModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title"><i class="fas fa-landmark"></i> 京韵遗风 · 管理后台</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="关闭"></button>
      </div>
      <div class="modal-body">
        <div class="intro-logo">
          <img src="/images/logo.png" alt="京韵遗风" onerror="this.style.display='none'">
        </div>
        <p><strong>用途：</strong> 临时后台管理，用于项目演示、测试及初期运营。</p>
        <p><strong>主要功能：</strong></p>
        <ul>
          <li>用户管理（增删改查、角色筛选、模拟登录、头像上传、在线状态）</li>
          <li>活动管理（发布/编辑/删除、封面图上传、状态切换）</li>
          <li>作品管理（增删改查、按传承人筛选、图片上传）</li>
          <li>非遗项目管理（分类/级别筛选、图片上传）</li>
          <li>招募管理（发布/编辑/删除、申请人数统计）</li>
          <li>评论管理（关键词搜索、一键删除）</li>
          <li>系统状态（实时统计、在线人数、服务器信息）</li>
        </ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn-jingyun" data-bs-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>

<!-- 退出后台按钮 -->
<div class="logout-btn" id="logoutBtn" title="退出后台">
  <i class="fas fa-sign-out-alt"></i>
</div>

<div class="theme-switcher">
  <div class="theme-btn light active" data-theme="light" title="日间模式"></div>
  <div class="theme-btn dark" data-theme="dark" title="夜间模式"></div>
  <div class="theme-btn soft" data-theme="soft" title="护眼模式"></div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="/js/common.js"></script>
<script>
  window.showSystemIntro = function() {};

  (function() {
    var brand = document.querySelector('.navbar-brand');
    if (brand) {
      brand.removeEventListener('click', window.showSystemIntro);
      brand.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var modal = new bootstrap.Modal(document.getElementById('adminAboutModal'));
        modal.show();
      });
    }
  })();

  document.getElementById('logoutBtn').addEventListener('click', function() {
    if (confirm('确定要退出管理后台吗？')) {
      window.location.href = '/manage/logout';
    }
  });

  function loadStats() {
    fetch('/manage/api/stats')
      .then(function(res) { return res.json(); })
      .then(function(data) {
        if (data.code === 200 && data.data) {
          var stats = data.data;
          fetch('/manage/api/online-count')
            .then(function(res2) { return res2.json(); })
            .then(function(onlineData) {
              var onlineCount = (onlineData.code === 200) ? onlineData.data : 0;
              var container = document.getElementById('statsContainer');
              container.innerHTML =
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-users"></i></div><div class="stat-number">' + (stats.totalUsers || 0) + '</div><div class="stat-label">总用户数</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-landmark"></i></div><div class="stat-number">' + (stats.totalItems || 0) + '</div><div class="stat-label">非遗项目</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-map-marker-alt"></i></div><div class="stat-number">' + (stats.totalSites || 0) + '</div><div class="stat-label">非遗点位</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-calendar-alt"></i></div><div class="stat-number">' + (stats.totalActivities || 0) + '</div><div class="stat-label">活动总数</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-heart"></i></div><div class="stat-number">' + (stats.totalCollections || 0) + '</div><div class="stat-label">收藏总数</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-handshake"></i></div><div class="stat-number">' + (stats.totalRecruits || 0) + '</div><div class="stat-label">招募数</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-comment"></i></div><div class="stat-number">' + (stats.totalComments || 0) + '</div><div class="stat-label">评论数</div></div></div>' +
                '<div class="col-md-6 col-lg-3"><div class="stat-card-enhanced"><div class="stat-icon"><i class="fas fa-chart-line"></i></div><div class="stat-number">' + onlineCount + '</div><div class="stat-label">当前在线人数</div></div></div>';
            });
        } else {
          document.getElementById('statsContainer').innerHTML = '<div class="col-12 text-center text-danger">加载统计数据失败</div>';
        }
      })
      .catch(function(err) {
        console.error(err);
        document.getElementById('statsContainer').innerHTML = '<div class="col-12 text-center text-danger">加载失败</div>';
      });
  }

  function loadSystemInfo() {
    fetch('/manage/api/system-info')
      .then(function(res) { return res.json(); })
      .then(function(data) {
        if (data.code === 200 && data.data) {
          var info = data.data;
          var html =
            '<div class="info-item"><span class="info-label">服务器时间：</span><span class="info-value">' + (info.serverTime || '-') + '</span></div>' +
            '<div class="info-item"><span class="info-label">操作系统：</span><span class="info-value">' + (info.osName || '-') + '</span></div>' +
            '<div class="info-item"><span class="info-label">CPU核心数：</span><span class="info-value">' + (info.availableProcessors || '-') + '</span></div>' +
            '<div class="info-item"><span class="info-label">JVM内存：</span><span class="info-value">已用 ' + (info.usedMemoryMB || 0) + ' MB / 总 ' + (info.totalMemoryMB || 0) + ' MB</span></div>' +
            '<div class="info-item"><span class="info-label">Java版本：</span><span class="info-value">' + (info.javaVersion || '-') + '</span></div>' +
            '<div class="info-item"><span class="info-label">工作目录：</span><span class="info-value">' + (info.userDir || '-') + '</span></div>';
          document.getElementById('serverInfoDetails').innerHTML = html;
        } else {
          document.getElementById('serverInfoDetails').innerHTML = '<div class="text-center text-danger">加载服务器信息失败</div>';
        }
      })
      .catch(function(err) {
        console.error(err);
        document.getElementById('serverInfoDetails').innerHTML = '<div class="text-center text-danger">加载失败</div>';
      });
  }

  document.addEventListener('DOMContentLoaded', function() {
    loadStats();
    loadSystemInfo();
  });
</script>
</body>
</html>