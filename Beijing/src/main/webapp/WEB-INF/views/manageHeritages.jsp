<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>京韵遗风·管理后台 - 非遗项目管理</title>
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
      <a href="/manage/activities" class="nav-link-jingyun"><i class="fas fa-calendar-alt"></i> 活动管理</a>
      <a href="/manage/artworks" class="nav-link-jingyun"><i class="fas fa-palette"></i> 作品管理</a>
      <a href="/manage/heritages" class="nav-link-jingyun active"><i class="fas fa-landmark"></i> 非遗项目</a>
      <a href="/manage/recruits" class="nav-link-jingyun"><i class="fas fa-handshake"></i> 招募管理</a>
      <a href="/manage/comments" class="nav-link-jingyun"><i class="fas fa-comment"></i> 评论管理</a>
    </div>
  </nav>

  <div class="page-header">
    <h1>非·遗·项·目</h1>
    <p>查看、筛选、添加、编辑、删除非遗项目</p>
  </div>

  <div class="filter-bar row g-3">
    <div class="col-md-4">
      <input type="text" id="keyword" class="form-control" placeholder="项目名称/描述">
    </div>
    <div class="col-md-3">
      <select id="categoryFilter" class="form-select">
        <option value="">全部分类</option>
        <option value="传统技艺">传统技艺</option>
        <option value="曲艺">曲艺</option>
        <option value="传统戏剧">传统戏剧</option>
        <option value="民间文学">民间文学</option>
        <option value="传统美术">传统美术</option>
        <option value="传统医药">传统医药</option>
        <option value="民俗">民俗</option>
      </select>
    </div>
    <div class="col-md-3">
      <select id="levelFilter" class="form-select">
        <option value="">全部级别</option>
        <option value="国家级">国家级</option>
        <option value="省级">省级</option>
        <option value="市级">市级</option>
      </select>
    </div>
    <div class="col-md-2">
      <button class="btn-jingyun" id="searchBtn"><i class="fas fa-search"></i> 筛选</button>
      <button class="btn-jingyun-outline ms-2" id="addHeritageBtn"><i class="fas fa-plus"></i> 添加项目</button>
    </div>
  </div>

  <div class="table-responsive">
    <table class="table table-hover table-manage">
      <thead>
        <tr>
          <th>ID</th>
          <th>图片</th>
          <th>名称</th>
          <th>类别</th>
          <th>地区</th>
          <th>级别</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody id="heritageTbody"></tbody>
    </table>
  </div>
  <div class="footer-note mt-4"><i class="fas fa-copyright"></i> 京韵遗风 · 数字博物馆</div>
</div>

<!-- 添加/编辑非遗项目模态框 -->
<div class="modal fade modal-jingyun" id="heritageModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="heritageModalTitle">添加非遗项目</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <form id="heritageForm">
          <input type="hidden" id="heritageId">
          <div class="mb-3">
            <label class="form-label">项目名称</label>
            <input type="text" class="form-control" id="heritageName" required>
          </div>
          <div class="mb-3">
            <label class="form-label">类别</label>
            <select class="form-select" id="heritageCategory">
              <option value="传统技艺">传统技艺</option>
              <option value="曲艺">曲艺</option>
              <option value="传统戏剧">传统戏剧</option>
              <option value="民间文学">民间文学</option>
              <option value="传统美术">传统美术</option>
              <option value="传统医药">传统医药</option>
              <option value="民俗">民俗</option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-label">地区</label>
            <input type="text" class="form-control" id="heritageRegion" placeholder="如：东城区">
          </div>
          <div class="mb-3">
            <label class="form-label">级别</label>
            <select class="form-select" id="heritageLevel">
              <option value="国家级">国家级</option>
              <option value="省级">省级</option>
              <option value="市级">市级</option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-label">描述</label>
            <textarea class="form-control" id="heritageDesc" rows="3"></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">项目图片</label>
            <div class="d-flex align-items-center gap-2">
              <input type="file" class="form-control" id="heritageImageFile" accept="image/*">
              <span class="text-muted small" id="heritageFileName"></span>
            </div>
            <div id="currentHeritageImagePreview" class="mt-2" style="display:none;">
              <img src="" style="max-width:150px; border-radius:8px;">
              <small class="text-muted ms-2">当前图片</small>
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
        <button type="button" class="btn btn-jingyun" id="saveHeritageBtn">保存</button>
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

  var heritageModal = null, currentHeritageImageUrl = '';

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
      if (m === '&') return '&amp;';
      if (m === '<') return '&lt;';
      if (m === '>') return '&gt;';
      return m;
    });
  }

  function loadHeritages() {
    var keyword = document.getElementById('keyword').value,
        category = document.getElementById('categoryFilter').value,
        level = document.getElementById('levelFilter').value,
        url = '/manage/api/heritages?';
    if (keyword) url += 'keyword=' + encodeURIComponent(keyword) + '&';
    if (category) url += 'category=' + category + '&';
    if (level) url += 'level=' + level;
    fetch(url)
      .then(function (res) { return res.json(); })
      .then(function (data) {
        var tbody = document.getElementById('heritageTbody');
        tbody.innerHTML = '';
        if (data.data && data.data.length > 0) {
          for (var i = 0; i < data.data.length; i++) {
            var h = data.data[i];
            var imgHtml = h.image_url ? '<img src="' + h.image_url + '" class="preview-img">' : '<i class="fas fa-image fa-2x text-muted"></i>';
            var row = '<tr>' +
              '<td>' + h.id + '</td>' +
              '<td>' + imgHtml + '</td>' +
              '<td>' + escapeHtml(h.name || '-') + '</td>' +
              '<td>' + escapeHtml(h.category || '-') + '</td>' +
              '<td>' + escapeHtml(h.region || '-') + '</td>' +
              '<td>' + escapeHtml(h.level || '-') + '</td>' +
              '<td><button class="btn btn-sm btn-warning edit-heritage" data-id="' + h.id + '"><i class="fas fa-edit"></i> 编辑</button> <button class="btn btn-sm btn-danger delete-heritage" data-id="' + h.id + '"><i class="fas fa-trash"></i> 删除</button></td>' +
              '</tr>';
            tbody.insertAdjacentHTML('beforeend', row);
          }
          document.querySelectorAll('.edit-heritage').forEach(function (btn) {
            btn.addEventListener('click', function () { editHeritage(this.dataset.id); });
          });
          document.querySelectorAll('.delete-heritage').forEach(function (btn) {
            btn.addEventListener('click', function () { deleteHeritage(this.dataset.id); });
          });
        } else {
          tbody.innerHTML = '<tr><td colspan="7" class="text-center">暂无非遗项目数据</td></tr>';
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

  function openHeritageModal(isEdit, h) {
    if (!heritageModal) heritageModal = new bootstrap.Modal(document.getElementById('heritageModal'));
    if (isEdit && h) {
      document.getElementById('heritageModalTitle').innerText = '编辑非遗项目';
      document.getElementById('heritageId').value = h.id;
      document.getElementById('heritageName').value = h.name || '';
      document.getElementById('heritageCategory').value = h.category || '传统技艺';
      document.getElementById('heritageRegion').value = h.region || '';
      document.getElementById('heritageLevel').value = h.level || '国家级';
      document.getElementById('heritageDesc').value = h.description || '';
      currentHeritageImageUrl = h.image_url || '';
      if (currentHeritageImageUrl) {
        document.getElementById('currentHeritageImagePreview').style.display = 'block';
        document.getElementById('currentHeritageImagePreview').querySelector('img').src = currentHeritageImageUrl;
      } else {
        document.getElementById('currentHeritageImagePreview').style.display = 'none';
      }
    } else {
      document.getElementById('heritageModalTitle').innerText = '添加非遗项目';
      document.getElementById('heritageId').value = '';
      document.getElementById('heritageForm').reset();
      document.getElementById('currentHeritageImagePreview').style.display = 'none';
      currentHeritageImageUrl = '';
    }
    document.getElementById('heritageImageFile').value = '';
    document.getElementById('heritageFileName').innerText = '';
    heritageModal.show();
  }

  function editHeritage(id) {
    fetch('/manage/api/heritages')
      .then(function (res) { return res.json(); })
      .then(function (data) {
        var h = data.data.find(function (i) { return i.id == id; });
        if (h) openHeritageModal(true, h);
        else alert('项目不存在');
      });
  }

  function deleteHeritage(id) {
    if (confirm('确定删除该非遗项目吗？')) {
      fetch('/manage/api/heritages/delete?heritageId=' + id, { method: 'POST' })
        .then(function (res) { return res.json(); })
        .then(function (data) { alert(data.msg); loadHeritages(); })
        .catch(function () { alert('删除失败'); });
    }
  }

  document.getElementById('heritageImageFile').addEventListener('change', function (e) {
    var f = e.target.files[0];
    document.getElementById('heritageFileName').innerText = f ? f.name : '';
  });

  document.getElementById('addHeritageBtn').addEventListener('click', function () { openHeritageModal(false, null); });

  document.getElementById('saveHeritageBtn').addEventListener('click', async function () {
    var id = document.getElementById('heritageId').value,
        name = document.getElementById('heritageName').value.trim(),
        category = document.getElementById('heritageCategory').value,
        region = document.getElementById('heritageRegion').value.trim(),
        level = document.getElementById('heritageLevel').value,
        description = document.getElementById('heritageDesc').value.trim(),
        imageFile = document.getElementById('heritageImageFile').files[0];
    if (!name) { alert('项目名称不能为空'); return; }
    try {
      var imgUrl = currentHeritageImageUrl;
      if (imageFile) imgUrl = await uploadImage(imageFile, 'heritage');
      var data = {
        name: name,
        category: category,
        region: region,
        level: level,
        description: description,
        image_url: imgUrl
      };
      var url = id ? '/manage/api/heritages/' + id : '/manage/api/heritages/add';
      var method = id ? 'PUT' : 'POST';
      var res = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
      var result = await res.json();
      alert(result.msg);
      if (heritageModal) heritageModal.hide();
      loadHeritages();
    } catch (err) { alert('保存失败：' + err.message); }
  });

  document.getElementById('searchBtn').addEventListener('click', loadHeritages);
  loadHeritages();
</script>
</body>
</html>