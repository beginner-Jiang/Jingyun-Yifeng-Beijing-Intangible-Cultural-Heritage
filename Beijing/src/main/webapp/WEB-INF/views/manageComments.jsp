<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>京韵遗风·管理后台 - 评论管理</title>
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

    .table-manage {
      background-color: #f7ede1 !important;
      border-radius: 1rem !important;
      overflow: hidden !important;
    }

    .table-manage > tbody,
    .table-manage > tbody > tr,
    .table-manage > tbody > tr > td,
    .table-manage > thead > tr > th {
      background-color: #f7ede1 !important;
    }

    body.theme-dark .table-manage,
    body.theme-dark .table-manage > tbody,
    body.theme-dark .table-manage > tbody > tr,
    body.theme-dark .table-manage > tbody > tr > td,
    body.theme-dark .table-manage > thead > tr > th {
      background-color: #2d2d2d !important;
      color: #e0e0e0;
    }

    body.theme-soft .table-manage,
    body.theme-soft .table-manage > tbody,
    body.theme-soft .table-manage > tbody > tr,
    body.theme-soft .table-manage > tbody > tr > td,
    body.theme-soft .table-manage > thead > tr > th {
      background-color: #fff8e7 !important;
      color: #5f4b3a;
    }

    .comment-content {
      max-width: 400px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .filter-bar {
      background: rgba(247, 237, 225, 0.8);
      border-radius: 2rem;
      padding: 1rem;
      margin-bottom: 1.5rem;
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
      <a href="/manage/system-info" class="nav-link-jingyun"><i class="fas fa-server"></i> 系统状态</a>
      <a href="/manage/users" class="nav-link-jingyun"><i class="fas fa-users"></i> 用户管理</a>
      <a href="/manage/activities" class="nav-link-jingyun"><i class="fas fa-calendar-alt"></i> 活动管理</a>
      <a href="/manage/artworks" class="nav-link-jingyun"><i class="fas fa-palette"></i> 作品管理</a>
      <a href="/manage/heritages" class="nav-link-jingyun"><i class="fas fa-landmark"></i> 非遗项目</a>
      <a href="/manage/recruits" class="nav-link-jingyun"><i class="fas fa-handshake"></i> 招募管理</a>
      <a href="/manage/comments" class="nav-link-jingyun active"><i class="fas fa-comment"></i> 评论管理</a>
    </div>
  </nav>

  <div class="page-header">
    <h1>评·论·管·理</h1>
    <p>查看、搜索、删除评论</p>
  </div>

  <div class="filter-bar row g-3">
    <div class="col-md-5">
      <input type="text" id="keyword" class="form-control" placeholder="评论内容/用户名">
    </div>
    <div class="col-md-4">
      <input type="number" id="userIdFilter" class="form-control" placeholder="用户ID">
    </div>
    <div class="col-md-3">
      <button class="btn-jingyun" id="searchBtn"><i class="fas fa-search"></i> 筛选</button>
    </div>
  </div>

  <div class="table-responsive">
    <table class="table table-hover table-manage">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户ID</th>
          <th>用户名</th>
          <th>内容</th>
          <th>时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody id="commentTbody"></tbody>
    </table>
  </div>
  <div class="footer-note mt-4"><i class="fas fa-copyright"></i> 京韵遗风 · 数字博物馆</div>
</div>

<!-- 独立的管理后台说明弹窗 -->
<div class="modal fade system-intro-modal" id="adminAboutModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title"><i class="fas fa-landmark"></i> 京韵遗风 · 管理后台</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
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
<div class="logout-btn" id="logoutBtn" title="退出后台"><i class="fas fa-sign-out-alt"></i></div>

<div class="theme-switcher">
  <div class="theme-btn light active" data-theme="light"></div>
  <div class="theme-btn dark" data-theme="dark"></div>
  <div class="theme-btn soft" data-theme="soft"></div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="/js/common.js"></script>
<script>
  window.showSystemIntro = function () {};

  (function () {
    var brand = document.querySelector('.navbar-brand');
    if (brand) {
      brand.removeEventListener('click', window.showSystemIntro);
      brand.addEventListener('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        new bootstrap.Modal(document.getElementById('adminAboutModal')).show();
      });
    }
  })();

  document.getElementById('logoutBtn').addEventListener('click', function () {
    if (confirm('确定要退出管理后台吗？')) {
      window.location.href = '/manage/logout';
    }
  });

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
      if (m === '&') return '&amp;';
      if (m === '<') return '&lt;';
      if (m === '>') return '&gt;';
      return m;
    });
  }

  function loadComments() {
    var keyword = document.getElementById('keyword').value,
        userId = document.getElementById('userIdFilter').value,
        url = '/manage/api/comments?';
    if (keyword) url += 'keyword=' + encodeURIComponent(keyword) + '&';
    if (userId) url += 'userId=' + userId;
    fetch(url)
      .then(function (res) { return res.json(); })
      .then(function (data) {
        var tbody = document.getElementById('commentTbody');
        tbody.innerHTML = '';
        if (data.data && data.data.length > 0) {
          for (var i = 0; i < data.data.length; i++) {
            var c = data.data[i];
            var row = '<tr>' +
              '<td>' + c.id + '</td>' +
              '<td>' + c.user_id + '</td>' +
              '<td>' + escapeHtml(c.username || '-') + '</td>' +
              '<td><div class="comment-content" title="' + escapeHtml(c.content) + '">' + escapeHtml(c.content) + '</div></td>' +
              '<td>' + (c.created_at || '-') + '</td>' +
              '<td><button class="btn btn-sm btn-danger delete-comment" data-id="' + c.id + '">删除</button></td>' +
              '</tr>';
            tbody.insertAdjacentHTML('beforeend', row);
          }
          document.querySelectorAll('.delete-comment').forEach(function (btn) {
            btn.addEventListener('click', function () { deleteComment(this.dataset.id); });
          });
        } else {
          tbody.innerHTML = '<tr><td colspan="6" class="text-center">暂无评论数据</td></tr>';
        }
      })
      .catch(function (err) { console.error(err); });
  }

  function deleteComment(id) {
    if (confirm('删除评论？')) {
      fetch('/manage/api/comments/delete?commentId=' + id, { method: 'POST' })
        .then(function (res) { return res.json(); })
        .then(function (data) { alert(data.msg); loadComments(); })
        .catch(function () { alert('删除失败'); });
    }
  }

  document.getElementById('searchBtn').addEventListener('click', loadComments);
  loadComments();
</script>
</body>
</html>