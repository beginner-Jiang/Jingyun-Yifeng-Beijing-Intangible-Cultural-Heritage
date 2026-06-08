<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>京韵遗风 · 管理后台登录</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="icon" href="/images/favicon.ico" type="image/x-icon">
  <script src="https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js"></script>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1.5rem;
      transition: background 0.3s, color 0.3s;
      position: relative;
      overflow: hidden;
    }

    #particles-js {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 1;
    }

    .theme-switcher {
      position: fixed;
      bottom: 2rem;
      left: 2rem;
      z-index: 1000;
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(4px);
      border-radius: 40px;
      padding: 0.5rem 1rem;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      display: flex;
      gap: 0.5rem;
    }

    .theme-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      border: 2px solid transparent;
      cursor: pointer;
      transition: 0.2s;
    }

    .theme-btn.light {
      background: #8b5a2b;
      border-color: #6b4a2a;
    }

    .theme-btn.dark {
      background: #2d2d2d;
      border-color: #888;
    }

    .theme-btn.soft {
      background: #fef9e7;
      border-color: #d6bc9a;
    }

    .theme-btn.active {
      transform: scale(1.2);
      box-shadow: 0 0 0 2px #b6844e;
    }

    .card-jingyun {
      background: rgba(255, 248, 235, 0.95);
      backdrop-filter: blur(10px);
      border: 2px solid #c9af8b;
      border-radius: 2.5rem;
      box-shadow: 0 30px 50px rgba(40, 25, 10, 0.5);
      max-width: 450px;
      width: 100%;
      padding: 2rem 2rem 2.5rem;
      text-align: center;
      transition: background 0.3s, border-color 0.3s, color 0.3s;
      z-index: 2;
      position: relative;
    }

    .logo-img {
      width: 180px;
      margin-bottom: 1.5rem;
    }

    .logo-text {
      font-size: 2.5rem;
      font-weight: 700;
      color: #4a2e1e;
      font-family: 'STKaiti', serif;
      margin-bottom: 0.5rem;
    }

    .form-group {
      margin-bottom: 1.2rem;
      text-align: left;
    }

    .form-group label {
      color: #5f432b;
      font-weight: 500;
      margin-bottom: 0.3rem;
      display: block;
    }

    .form-control {
      border: 1px solid #c6a77c;
      border-radius: 40px;
      padding: 0.6rem 1.2rem;
      background: #fffcf2;
      transition: background 0.3s, border-color 0.3s, color 0.3s;
    }

    .btn-login {
      width: 100%;
      background: #b6844e;
      border: none;
      border-radius: 40px;
      padding: 0.7rem;
      color: white;
      font-size: 1.2rem;
      font-weight: 600;
      margin-top: 1rem;
      transition: 0.2s;
    }

    .btn-login:hover {
      background: #c79b65;
    }

    .error-msg {
      color: #dc3545;
      margin-top: 1rem;
      font-size: 0.9rem;
    }

    body.theme-light {
      background: linear-gradient(145deg, #3e2c1a 0%, #6b4f32 100%) !important;
    }

    body.theme-light .card-jingyun {
      background: rgba(255, 248, 235, 0.95);
      border-color: #c9af8b;
    }

    body.theme-light .logo-text {
      color: #4a2e1e;
    }

    body.theme-light .form-group label {
      color: #5f432b;
    }

    body.theme-light .form-control {
      background: #fffcf2;
      border-color: #c6a77c;
      color: #2e1e12;
    }

    body.theme-light .btn-login {
      background: #b6844e;
    }

    body.theme-light .btn-login:hover {
      background: #c79b65;
    }

    body.theme-dark {
      background: #1e1e1e !important;
      color: #e0e0e0;
    }

    body.theme-dark .card-jingyun {
      background: #2d2d2d;
      border-color: #5a4a3a;
    }

    body.theme-dark .logo-text {
      color: #e0e0e0;
    }

    body.theme-dark .form-group label {
      color: #e0e0e0;
    }

    body.theme-dark .form-control {
      background: #3d3d3d;
      border-color: #5a4a3a;
      color: #e0e0e0;
    }

    body.theme-dark .btn-login {
      background: #5a4a3a;
    }

    body.theme-dark .btn-login:hover {
      background: #7a5a3a;
    }

    body.theme-soft {
      background: #fef9e7 !important;
      color: #5f4b3a;
    }

    body.theme-soft .card-jingyun {
      background: #fff8e7;
      border-color: #d6bc9a;
    }

    body.theme-soft .logo-text {
      color: #5f4b3a;
    }

    body.theme-soft .form-group label {
      color: #5f4b3a;
    }

    body.theme-soft .form-control {
      background: #fff8e7;
      border-color: #d6bc9a;
      color: #5f4b3a;
    }

    body.theme-soft .btn-login {
      background: #c2a27e;
    }

    body.theme-soft .btn-login:hover {
      background: #d2b28e;
    }
  </style>
</head>
<body>
<div id="particles-js"></div>

<div class="card-jingyun">
  <img src="/images/logo.png" alt="Logo" class="logo-img">
  <div class="logo-text">京韵遗风</div>
  <div class="logo-sub" style="margin-bottom: 1.5rem;">管理后台登录</div>

  <form id="loginForm" action="${pageContext.request.contextPath}/manage/auth/login" method="post">
    <div class="form-group">
      <label><i class="fas fa-user"></i> 用户名</label>
      <input type="text" class="form-control" name="username" id="username" required autocomplete="off">
    </div>
    <div class="form-group">
      <label><i class="fas fa-lock"></i> 密码</label>
      <input type="password" class="form-control" name="password" id="password" required>
    </div>
    <button type="submit" class="btn-login"><i class="fas fa-sign-in-alt"></i> 登录管理后台</button>
  </form>
  <div id="errorMsg" class="error-msg">${error}</div>
</div>

<div class="theme-switcher">
  <div class="theme-btn light active" data-theme="light" title="日间模式"></div>
  <div class="theme-btn dark" data-theme="dark" title="夜间模式"></div>
  <div class="theme-btn soft" data-theme="soft" title="护眼模式"></div>
</div>

<script>
  function setTheme(theme) {
    var body = document.body;
    var btns = document.querySelectorAll('.theme-btn');
    body.classList.remove('theme-light', 'theme-dark', 'theme-soft');
    body.classList.add('theme-' + theme);
    btns.forEach(function(btn) {
      if (btn.dataset.theme === theme) {
        btn.classList.add('active');
      } else {
        btn.classList.remove('active');
      }
    });
    localStorage.setItem('adminTheme', theme);
    var particleColor = theme === 'light' ? '#b6844e' : (theme === 'dark' ? '#888888' : '#c2a27e');
    if (window.pJSDom && window.pJSDom[0]) {
      window.pJSDom[0].pJS.particles.color.value = particleColor;
      window.pJSDom[0].pJS.particles.line_linked.color = particleColor;
      window.pJSDom[0].pJS.fn.particlesRefresh();
    }
  }

  function initTheme() {
    var savedTheme = localStorage.getItem('adminTheme') || 'light';
    setTheme(savedTheme);
    document.querySelectorAll('.theme-btn').forEach(function(btn) {
      btn.addEventListener('click', function() {
        var theme = this.dataset.theme;
        if (theme) setTheme(theme);
      });
    });
  }

  particlesJS('particles-js', {
    particles: {
      number: { value: 80, density: { enable: true, value_area: 800 } },
      color: { value: '#b6844e' },
      shape: { type: 'circle' },
      opacity: { value: 0.5, random: true },
      size: { value: 3, random: true },
      line_linked: { enable: true, distance: 150, color: '#b6844e', opacity: 0.4, width: 1 },
      move: { enable: true, speed: 2, direction: 'none', random: true, straight: false, out_mode: 'out' }
    },
    interactivity: {
      detect_on: 'canvas',
      events: { onhover: { enable: true, mode: 'repulse' }, onclick: { enable: true, mode: 'push' } }
    }
  });

  if (window.location.search.indexOf('error') !== -1) {
    document.getElementById('errorMsg').innerText = '用户名或密码错误';
  }
  initTheme();
</script>
</body>
</html>