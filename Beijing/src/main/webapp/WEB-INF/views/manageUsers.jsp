<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>京韵遗风·管理后台 - 用户管理</title>
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

    .online-dot {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: #28a745;
      margin-right: 6px;
    }

    .offline-dot {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: #aaa;
      margin-right: 6px;
    }

    .btn-impersonate:disabled,
    .edit-user:disabled,
    .delete-user:disabled {
      opacity: 0.6;
      cursor: not-allowed;
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
      <a href="/manage/users" class="nav-link-jingyun active"><i class="fas fa-users"></i> 用户管理</a>
      <a href="/manage/activities" class="nav-link-jingyun"><i class="fas fa-calendar-alt"></i> 活动管理</a>
      <a href="/manage/artworks" class="nav-link-jingyun"><i class="fas fa-palette"></i> 作品管理</a>
      <a href="/manage/heritages" class="nav-link-jingyun"><i class="fas fa-landmark"></i> 非遗项目</a>
      <a href="/manage/recruits" class="nav-link-jingyun"><i class="fas fa-handshake"></i> 招募管理</a>
      <a href="/manage/comments" class="nav-link-jingyun"><i class="fas fa-comment"></i> 评论管理</a>
    </div>
  </nav>

  <div class="page-header">
    <h1>用·户·管·理</h1>
    <p>查看、筛选、添加、编辑、模拟登录或删除用户</p>
  </div>

  <div class="filter-bar row g-3">
    <div class="col-md-3">
      <input type="text" id="keyword" class="form-control" placeholder="用户名/邮箱/手机">
    </div>
    <div class="col-md-2">
      <select id="role" class="form-select">
        <option value="">全部角色</option>
        <option value="user">普通用户</option>
        <option value="inheritor">传承人</option>
        <option value="admin">管理员</option>
      </select>
    </div>
    <div class="col-md-2">
      <input type="number" id="minAge" class="form-control" placeholder="最小年龄">
    </div>
    <div class="col-md-2">
      <input type="number" id="maxAge" class="form-control" placeholder="最大年龄">
    </div>
    <div class="col-md-3">
      <button class="btn-jingyun" id="searchBtn"><i class="fas fa-search"></i> 筛选</button>
      <button class="btn-jingyun-outline ms-2" id="addUserBtn"><i class="fas fa-plus"></i> 添加用户</button>
    </div>
  </div>

  <div class="table-responsive">
    <table class="table table-hover table-manage">
      <thead>
        <tr>
          <th>ID</th>
          <th>头像</th>
          <th>用户名</th>
          <th>邮箱</th>
          <th>手机</th>
          <th>年龄</th>
          <th>角色</th>
          <th>在线状态</th>
          <th>注册时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody id="userTableBody"></tbody>
    </table>
  </div>
  <div class="footer-note mt-4"><i class="fas fa-copyright"></i> 京韵遗风 · 数字博物馆  |  管理后台</div>
</div>

<!-- 添加/编辑用户模态框 -->
<div class="modal fade" id="userModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="userModalTitle">添加用户</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <form id="userForm">
          <input type="hidden" id="userId">
          <div class="text-center mb-3">
            <img src="" id="avatarPreview" class="avatar-preview" style="width:100px;height:100px;border-radius:50%;object-fit:cover;border:3px solid #b6844e;">
            <div><label for="avatarUpload" class="btn btn-sm btn-outline-secondary mt-2">上传头像</label><input type="file" id="avatarUpload" accept="image/*" style="display:none"></div>
          </div>
          <div class="mb-3">
            <label class="form-label">用户名</label>
            <input type="text" class="form-control" id="userUsername" required>
          </div>
          <div class="mb-3">
            <label class="form-label">邮箱</label>
            <input type="email" class="form-control" id="userEmail">
          </div>
          <div class="mb-3">
            <label class="form-label">手机号</label>
            <input type="tel" class="form-control" id="userPhone">
          </div>
          <div class="mb-3">
            <label class="form-label">年龄</label>
            <input type="number" class="form-control" id="userAge" min="1" max="120">
          </div>
          <div class="mb-3">
            <label class="form-label">角色</label>
            <select class="form-select" id="userRole">
              <option value="user">普通用户</option>
              <option value="inheritor">传承人</option>
              <option value="admin">管理员</option>
            </select>
          </div>
          <div class="mb-3" id="passwordField">
            <label class="form-label">密码</label>
            <input type="password" class="form-control" id="userPassword" placeholder="至少6位">
            <small class="text-muted">编辑时留空则不修改密码</small>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
        <button type="button" class="btn btn-jingyun" id="saveUserBtn">保存</button>
      </div>
    </div>
  </div>
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
        <div class="intro-logo"><img src="/images/logo.png" alt="京韵遗风" onerror="this.style.display='none'"></div>
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
  window.showSystemIntro = function() {};

  (function() {
    var brand = document.querySelector('.navbar-brand');
    if (brand) {
      brand.removeEventListener('click', window.showSystemIntro);
      brand.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        new bootstrap.Modal(document.getElementById('adminAboutModal')).show();
      });
    }
  })();

  document.getElementById('logoutBtn').addEventListener('click', function() {
    if (confirm('确定要退出管理后台吗？')) {
      window.location.href = '/manage/logout';
    }
  });

  var userModal = null;
  var currentAvatarFile = null;
  var currentAvatarUrl = '';

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
      if (m === '&') return '&amp;';
      if (m === '<') return '&lt;';
      if (m === '>') return '&gt;';
      return m;
    });
  }

  async function uploadImage(file, type) {
    var formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    var response = await fetch('/manage/api/upload', { method: 'POST', body: formData });
    var result = await response.json();
    if (result.url) return result.url;
    throw new Error(result.msg || '上传失败');
  }

  function showAdminForbiddenMsg(operation) {
    alert('模拟管理员账号无法进行「' + operation + '」操作。');
  }

  function loadUsers() {
    var keyword = document.getElementById('keyword').value,
        role = document.getElementById('role').value,
        minAge = document.getElementById('minAge').value,
        maxAge = document.getElementById('maxAge').value;
    var url = '/manage/api/users?';
    if (keyword) url += 'keyword=' + encodeURIComponent(keyword) + '&';
    if (role) url += 'role=' + role + '&';
    if (minAge) url += 'minAge=' + minAge + '&';
    if (maxAge) url += 'maxAge=' + maxAge;
    fetch(url)
      .then(function(res) { return res.json(); })
      .then(function(data) {
        var tbody = document.getElementById('userTableBody');
        tbody.innerHTML = '';
        if (data.data && data.data.length === 0) {
          tbody.innerHTML = '<td><td colspan="10" class="text-center">暂无用户</td></tr>';
          return;
        }
        for (var i = 0; i < data.data.length; i++) {
          var u = data.data[i];
          var isAdmin = (u.username === 'admin');
          var roleText = u.role == 'user' ? '普通' : (u.role == 'inheritor' ? '传承人' : '管理员');
          var avatarHtml = isAdmin ? '<i class="fas fa-user-shield fa-2x text-muted"></i>' : ('<img src="' + (u.avatar || 'https://picsum.photos/40/40?random=' + u.id) + '" style="width:40px;height:40px;border-radius:50%;object-fit:cover;">');
          var onlineStatus = isAdmin ? '-' : (u.online ? '<span class="online-dot"></span>在线' : '<span class="offline-dot"></span>离线');
          var regTime = isAdmin ? '-' : (u.created_at || '-');
          var emailDisplay = isAdmin ? '-' : (escapeHtml(u.email || '-'));
          var phoneDisplay = isAdmin ? '-' : (escapeHtml(u.phone || '-'));
          var ageDisplay = isAdmin ? '-' : (u.age || '-');
          var impersonateDisabled = isAdmin ? 'disabled' : '';
          var editDisabled = isAdmin ? 'disabled' : '';
          var deleteDisabled = isAdmin ? 'disabled' : '';
          var impersonateOnclick = isAdmin ? 'showAdminForbiddenMsg("模拟登录")' : 'impersonate(' + u.id + ', \'' + escapeHtml(u.username) + '\')';
          var editOnclick = isAdmin ? 'showAdminForbiddenMsg("编辑")' : 'editUser(' + u.id + ')';
          var deleteOnclick = isAdmin ? 'showAdminForbiddenMsg("删除")' : 'deleteUser(' + u.id + ')';
          var row = '<tr>' +
            '<td>' + u.id + '</td>' +
            '<td>' + avatarHtml + '</td>' +
            '<td>' + escapeHtml(u.username) + '</td>' +
            '<td>' + emailDisplay + '</td>' +
            '<td>' + phoneDisplay + '</td>' +
            '<td>' + ageDisplay + '</td>' +
            '<td>' + roleText + '</td>' +
            '<td>' + onlineStatus + '</td>' +
            '<td>' + regTime + '</td>' +
            '<td><button class="btn btn-sm btn-impersonate" onclick="' + impersonateOnclick + '" ' + impersonateDisabled + '><i class="fas fa-exchange-alt"></i> 模拟</button> <button class="btn btn-sm btn-warning edit-user" onclick="' + editOnclick + '" ' + editDisabled + '><i class="fas fa-edit"></i> 编辑</button> <button class="btn btn-sm btn-danger delete-user" onclick="' + deleteOnclick + '" ' + deleteDisabled + '><i class="fas fa-trash"></i> 删除</button></td>' +
            '</tr>';
          tbody.insertAdjacentHTML('beforeend', row);
        }
      })
      .catch(function(err) { console.error(err); });
  }

  function impersonate(userId, username) {
    if (confirm('以 "' + username + '" 身份登录？')) {
      fetch('/manage/api/impersonate?userId=' + userId, { method: 'POST' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
          if (data.token) {
            localStorage.setItem('token', data.token);
            alert('登录成功');
            window.location.href = '/Digital.html';
          } else {
            alert(data.msg || '模拟登录失败');
          }
        })
        .catch(function() { alert('网络错误'); });
    }
  }

  function deleteUser(userId) {
    if (confirm('永久删除该用户及其所有数据？不可恢复！')) {
      fetch('/manage/api/users/delete?userId=' + userId, { method: 'POST' })
        .then(function(res) { return res.json(); })
        .then(function(data) { alert(data.msg); loadUsers(); })
        .catch(function() { alert('删除失败'); });
    }
  }

  function openUserModal(isEdit, userData) {
    if (!userModal) userModal = new bootstrap.Modal(document.getElementById('userModal'));
    if (isEdit && userData) {
      document.getElementById('userModalTitle').innerText = '编辑用户';
      document.getElementById('userId').value = userData.id;
      document.getElementById('userUsername').value = userData.username || '';
      document.getElementById('userEmail').value = userData.email || '';
      document.getElementById('userPhone').value = userData.phone || '';
      document.getElementById('userAge').value = userData.age || '';
      document.getElementById('userRole').value = userData.role || 'user';
      document.getElementById('userPassword').value = '';
      document.getElementById('passwordField').style.display = 'block';
      currentAvatarUrl = userData.avatar || '';
      document.getElementById('avatarPreview').src = currentAvatarUrl || 'https://picsum.photos/100/100?random=1';
    } else {
      document.getElementById('userModalTitle').innerText = '添加用户';
      document.getElementById('userId').value = '';
      document.getElementById('userForm').reset();
      document.getElementById('userRole').value = 'user';
      document.getElementById('passwordField').style.display = 'block';
      currentAvatarUrl = '';
      document.getElementById('avatarPreview').src = 'https://picsum.photos/100/100?random=1';
    }
    currentAvatarFile = null;
    userModal.show();
  }

  function editUser(userId) {
    fetch('/manage/api/users')
      .then(function(res) { return res.json(); })
      .then(function(data) {
        var user = data.data.find(function(u) { return u.id == userId; });
        if (user) openUserModal(true, user);
        else alert('用户不存在');
      });
  }

  document.getElementById('avatarUpload').addEventListener('change', function(e) {
    var file = e.target.files[0];
    if (file) {
      currentAvatarFile = file;
      var reader = new FileReader();
      reader.onload = function(ev) { document.getElementById('avatarPreview').src = ev.target.result; };
      reader.readAsDataURL(file);
    }
  });

  document.getElementById('addUserBtn').addEventListener('click', function() { openUserModal(false, null); });

  document.getElementById('saveUserBtn').addEventListener('click', async function() {
    var userId = document.getElementById('userId').value;
    var userData = {
      username: document.getElementById('userUsername').value.trim(),
      email: document.getElementById('userEmail').value.trim(),
      phone: document.getElementById('userPhone').value.trim(),
      age: parseInt(document.getElementById('userAge').value),
      role: document.getElementById('userRole').value,
      avatar: currentAvatarUrl
    };
    var password = document.getElementById('userPassword').value;
    if (!userData.username) { alert('用户名不能为空'); return; }
    try {
      if (currentAvatarFile) {
        userData.avatar = await uploadImage(currentAvatarFile, 'avatar');
      }
      var url, method;
      if (userId) {
        url = '/manage/api/users/' + userId;
        method = 'PUT';
      } else {
        url = '/manage/api/users/add';
        method = 'POST';
        if (!password || password.length < 6) {
          alert('密码至少6位');
          return;
        }
        userData.password = password;
      }
      var response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(userData) });
      var result = await response.json();
      alert(result.msg);
      if (userModal) userModal.hide();
      loadUsers();
    } catch (err) { alert('保存失败：' + err.message); }
  });

  document.getElementById('searchBtn').addEventListener('click', loadUsers);
  loadUsers();
</script>
</body>
</html>