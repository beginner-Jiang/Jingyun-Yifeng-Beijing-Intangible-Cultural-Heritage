<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>京韵遗风·管理后台 - 活动管理</title>
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

    .preview-img {
      width: 60px;
      height: 60px;
      object-fit: cover;
      border-radius: 8px;
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
      <a href="/manage/activities" class="nav-link-jingyun active"><i class="fas fa-calendar-alt"></i> 活动管理</a>
      <a href="/manage/artworks" class="nav-link-jingyun"><i class="fas fa-palette"></i> 作品管理</a>
      <a href="/manage/heritages" class="nav-link-jingyun"><i class="fas fa-landmark"></i> 非遗项目</a>
      <a href="/manage/recruits" class="nav-link-jingyun"><i class="fas fa-handshake"></i> 招募管理</a>
      <a href="/manage/comments" class="nav-link-jingyun"><i class="fas fa-comment"></i> 评论管理</a>
    </div>
  </nav>

  <div class="page-header">
    <h1>活·动·管·理</h1>
    <p>查看、筛选、添加、编辑、删除活动</p>
  </div>

  <div class="filter-bar row g-3">
    <div class="col-md-4">
      <input type="text" id="keyword" class="form-control" placeholder="活动标题/描述">
    </div>
    <div class="col-md-3">
      <select id="statusFilter" class="form-select">
        <option value="">全部状态</option>
        <option value="1">进行中</option>
        <option value="2">已结束</option>
        <option value="0">待审核</option>
      </select>
    </div>
    <div class="col-md-5">
      <button class="btn-jingyun" id="searchBtn"><i class="fas fa-search"></i> 筛选</button>
      <button class="btn-jingyun-outline ms-2" id="addActivityBtn"><i class="fas fa-plus"></i> 添加活动</button>
    </div>
  </div>

  <div class="table-responsive">
    <table class="table table-hover table-manage">
      <thead>
        <tr>
          <th>ID</th>
          <th>封面</th>
          <th>标题</th>
          <th>类型</th>
          <th>传承人ID</th>
          <th>状态</th>
          <th>报名数</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody id="activityTbody"></tbody>
    </table>
  </div>
  <div class="footer-note mt-4"><i class="fas fa-copyright"></i> 京韵遗风 · 数字博物馆</div>
</div>

<!-- 添加/编辑活动模态框 -->
<div class="modal fade modal-jingyun" id="activityModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="activityModalTitle">添加活动</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <form id="activityForm">
          <input type="hidden" id="activityId">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">活动标题</label>
              <input type="text" class="form-control" id="activityTitle" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">活动类型</label>
              <select class="form-select" id="activityType">
                <option value="体验课">体验课</option>
                <option value="讲座">讲座</option>
                <option value="展览">展览</option>
                <option value="演出">演出</option>
              </select>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">开始时间</label>
              <input type="datetime-local" class="form-control" id="activityStartTime" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">结束时间</label>
              <input type="datetime-local" class="form-control" id="activityEndTime" required>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">最大参与人数</label>
              <input type="number" class="form-control" id="activityMaxParticipants" value="20" min="1">
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">传承人ID</label>
              <input type="number" class="form-control" id="activityInheritorId" placeholder="可选">
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">活动描述</label>
            <textarea class="form-control" id="activityDesc" rows="3" required></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">活动封面</label>
            <div class="d-flex align-items-center gap-2">
              <input type="file" class="form-control" id="activityImageFile" accept="image/*">
              <span class="text-muted small" id="activityFileName"></span>
            </div>
            <div id="currentImagePreview" class="mt-2" style="display:none;">
              <img src="" style="max-width:150px; border-radius:8px;">
              <small class="text-muted ms-2">当前封面</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">状态</label>
            <select class="form-select" id="activityStatus">
              <option value="1">进行中</option>
              <option value="2">已结束</option>
              <option value="0">待审核</option>
            </select>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
        <button type="button" class="btn btn-jingyun" id="saveActivityBtn">保存</button>
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

  var activityModal = null, currentImageUrl = '';

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
      if (m === '&') return '&amp;';
      if (m === '<') return '&lt;';
      if (m === '>') return '&gt;';
      return m;
    });
  }

  function loadActivities() {
    var keyword = document.getElementById('keyword').value,
        status = document.getElementById('statusFilter').value,
        url = '/manage/api/activities?';
    if (keyword) url += 'keyword=' + encodeURIComponent(keyword) + '&';
    if (status) url += 'status=' + status;
    fetch(url)
      .then(function (res) { return res.json(); })
      .then(function (data) {
        var tbody = document.getElementById('activityTbody');
        tbody.innerHTML = '';
        if (data.data && data.data.length > 0) {
          for (var i = 0; i < data.data.length; i++) {
            var a = data.data[i];
            var statusText = a.status == 1 ? '进行中' : (a.status == 2 ? '已结束' : '待审核');
            var imgHtml = a.image_url ? '<img src="' + a.image_url + '" class="preview-img">' : '<i class="fas fa-image fa-2x text-muted"></i>';
            var row = '<tr>' +
              '<td>' + a.id + '</td>' +
              '<td>' + imgHtml + '</td>' +
              '<td>' + escapeHtml(a.title || '-') + '</td>' +
              '<td>' + escapeHtml(a.type || '-') + '</td>' +
              '<td>' + (a.inheritor_id || '-') + '</td>' +
              '<td>' + statusText + '</td>' +
              '<td>' + (a.current_participants || 0) + '/' + (a.max_participants || 0) + '</td>' +
              '<td><button class="btn btn-sm btn-warning edit-activity" data-id="' + a.id + '"><i class="fas fa-edit"></i> 编辑</button> <button class="btn btn-sm btn-danger delete-activity" data-id="' + a.id + '"><i class="fas fa-trash"></i> 删除</button></td>' +
              '</tr>';
            tbody.insertAdjacentHTML('beforeend', row);
          }
          document.querySelectorAll('.edit-activity').forEach(function (btn) {
            btn.addEventListener('click', function () { editActivity(this.dataset.id); });
          });
          document.querySelectorAll('.delete-activity').forEach(function (btn) {
            btn.addEventListener('click', function () { deleteActivity(this.dataset.id); });
          });
        } else {
          tbody.innerHTML = '<tr><td colspan="8" class="text-center">暂无活动数据</td></tr>';
        }
      })
      .catch(function (err) { console.error(err); });
  }

  async function uploadImage(file, type) {
    var fd = new FormData();
    fd.append('file', file);
    fd.append('type', type);
    var res = await fetch('/manage/api/upload', { method: 'POST', body: fd });
    var json = await res.json();
    if (json.url) return json.url;
    throw new Error(json.msg || '上传失败');
  }

  function openActivityModal(isEdit, act) {
    if (!activityModal) activityModal = new bootstrap.Modal(document.getElementById('activityModal'));
    if (isEdit && act) {
      document.getElementById('activityModalTitle').innerText = '编辑活动';
      document.getElementById('activityId').value = act.id;
      document.getElementById('activityTitle').value = act.title || '';
      document.getElementById('activityType').value = act.type || '体验课';
      document.getElementById('activityStartTime').value = act.start_time ? act.start_time.substring(0, 16) : '';
      document.getElementById('activityEndTime').value = act.end_time ? act.end_time.substring(0, 16) : '';
      document.getElementById('activityMaxParticipants').value = act.max_participants || 20;
      document.getElementById('activityInheritorId').value = act.inheritor_id || '';
      document.getElementById('activityDesc').value = act.description || '';
      document.getElementById('activityStatus').value = act.status || 1;
      currentImageUrl = act.image_url || '';
      if (currentImageUrl) {
        document.getElementById('currentImagePreview').style.display = 'block';
        document.getElementById('currentImagePreview').querySelector('img').src = currentImageUrl;
      } else {
        document.getElementById('currentImagePreview').style.display = 'none';
      }
    } else {
      document.getElementById('activityModalTitle').innerText = '添加活动';
      document.getElementById('activityForm').reset();
      document.getElementById('activityStatus').value = '1';
      document.getElementById('currentImagePreview').style.display = 'none';
      currentImageUrl = '';
    }
    document.getElementById('activityImageFile').value = '';
    document.getElementById('activityFileName').innerText = '';
    activityModal.show();
  }

  function editActivity(id) {
    fetch('/manage/api/activities')
      .then(function (res) { return res.json(); })
      .then(function (data) {
        var act = data.data.find(function (a) { return a.id == id; });
        if (act) openActivityModal(true, act);
        else alert('活动不存在');
      });
  }

  function deleteActivity(id) {
    if (confirm('确定删除该活动吗？')) {
      fetch('/manage/api/activities/delete?activityId=' + id, { method: 'POST' })
        .then(function (res) { return res.json(); })
        .then(function (data) { alert(data.msg); loadActivities(); })
        .catch(function () { alert('删除失败'); });
    }
  }

  document.getElementById('activityImageFile').addEventListener('change', function (e) {
    var f = e.target.files[0];
    document.getElementById('activityFileName').innerText = f ? f.name : '';
  });

  document.getElementById('addActivityBtn').addEventListener('click', function () { openActivityModal(false, null); });

  document.getElementById('saveActivityBtn').addEventListener('click', async function () {
    var id = document.getElementById('activityId').value,
        title = document.getElementById('activityTitle').value.trim(),
        type = document.getElementById('activityType').value,
        startTime = document.getElementById('activityStartTime').value,
        endTime = document.getElementById('activityEndTime').value,
        maxParticipants = parseInt(document.getElementById('activityMaxParticipants').value),
        inheritorId = document.getElementById('activityInheritorId').value ? parseInt(document.getElementById('activityInheritorId').value) : null,
        description = document.getElementById('activityDesc').value.trim(),
        status = parseInt(document.getElementById('activityStatus').value),
        imageFile = document.getElementById('activityImageFile').files[0];
    if (!title || !startTime || !endTime || !description) { alert('请填写必填项'); return; }
    try {
      var imgUrl = currentImageUrl;
      if (imageFile) imgUrl = await uploadImage(imageFile, 'activity');
      var data = {
        title: title,
        type: type,
        start_time: startTime,
        end_time: endTime,
        max_participants: maxParticipants,
        inheritor_id: inheritorId,
        description: description,
        image_url: imgUrl,
        status: status
      };
      var url = id ? '/manage/api/activities/' + id : '/manage/api/activities/add';
      var method = id ? 'PUT' : 'POST';
      var res = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
      var result = await res.json();
      alert(result.msg);
      if (activityModal) activityModal.hide();
      loadActivities();
    } catch (err) { alert('保存失败：' + err.message); }
  });

  document.getElementById('searchBtn').addEventListener('click', loadActivities);
  loadActivities();
</script>
</body>
</html>