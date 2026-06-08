// 公共函数 - 京韵遗风所有页面共用

// 全局配置
window.API_BASE_URL = 'http://localhost:8080/api';

// 工具函数
window.getToken = function() {
    return localStorage.getItem('token');
};

window.showMessage = function(msg, type = 'info') {
    alert('【' + type + '】 ' + msg);
};

window.fetchWithAuth = async function(url, options = {}) {
    const token = window.getToken();
    if (!token) {
        window.showMessage('请先登录', 'warning');
        window.location.href = 'Index.html';
        throw new Error('未登录');
    }
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };
    const response = await fetch(url, { ...options, headers });
    if (response.status === 401) {
        window.showMessage('登录已过期，请重新登录', 'warning');
        localStorage.removeItem('token');
        window.location.href = 'Index.html';
        throw new Error('未授权');
    }
    const result = await response.json();
    if (result.code !== 200) {
        throw new Error(result.msg || '请求失败');
    }
    return result.data;
};

window.uploadWithAuth = async function(url, formData) {
    const token = window.getToken();
    if (!token) {
        window.showMessage('请先登录', 'warning');
        window.location.href = 'Index.html';
        throw new Error('未登录');
    }
    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData
    });
    if (response.status === 401) {
        window.showMessage('登录已过期', 'warning');
        localStorage.removeItem('token');
        window.location.href = 'Index.html';
        throw new Error('未授权');
    }
    const result = await response.json();
    if (result.code !== 200) {
        throw new Error(result.msg || '请求失败');
    }
    return result.data;
};

// 主题切换
function setTheme(theme) {
    const body = document.body;
    const themeBtns = document.querySelectorAll('.theme-btn');
    body.className = body.className.replace(/theme-\S+/g, '').trim();
    body.classList.add(`theme-${theme}`);
    themeBtns.forEach(btn => {
        const btnTheme = btn.dataset.theme;
        if (btnTheme === theme) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });
    localStorage.setItem('theme', theme);
    console.log(`主题已设置为: ${theme}`);
}

function initTheme() {
    console.log('初始化主题');
    const themeBtns = document.querySelectorAll('.theme-btn');
    if (!themeBtns.length) {
        console.warn('未找到主题切换按钮');
        return;
    }
    const savedTheme = localStorage.getItem('theme') || 'light';
    setTheme(savedTheme);
    themeBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            const theme = this.dataset.theme;
            if (!theme) return;
            setTheme(theme);
        });
    });
    window.addEventListener('storage', function(e) {
        if (e.key === 'theme') {
            const newTheme = e.newValue || 'light';
            console.log(`检测到其他标签页主题变更: ${newTheme}`);
            setTheme(newTheme);
        }
    });
}

// 退出系统
function initExitConfirm() {
    const homeLink = document.querySelector('.nav-link-jingyun[href="Index.html"]');
    if (homeLink) {
        homeLink.addEventListener('click', function(e) {
            e.preventDefault();
            const currentPage = window.location.pathname.split('/').pop();
            if (currentPage !== 'Index.html') {
                if (confirm('是否退出系统？')) {
                    localStorage.removeItem('token');
                    window.location.href = 'Index.html';
                }
            } else {
                window.location.href = 'Index.html';
            }
        });
    }
}

// 系统介绍模态框
window.showSystemIntro = function() {
    console.log('showSystemIntro 被调用');
    let existingModal = document.getElementById('systemIntroModal');
    if (existingModal) {
        existingModal.remove();
    }

    const modalHTML = `
        <div class="modal fade system-intro-modal" id="systemIntroModal" tabindex="-1" aria-hidden="true" style="z-index: 1060;">
            <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-landmark"></i> 京韵遗风 · 数字博物馆
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="关闭"></button>
                    </div>
                    <div class="modal-body">
                        <div class="intro-logo">
                            <img src="/images/logo.png" alt="京韵遗风Logo" onerror="this.src='https://picsum.photos/180/180?random=logo'">
                        </div>
                        <div class="intro-text">
                            <p><strong>京韵遗风</strong> 是北京非物质文化遗产数字博物馆与传承人互动平台。</p>
                            <p>集数字展览、地图寻迹、AI创意生成、传承人互动、数据看板、个人护照于一体，让非遗“活”起来。</p>
                        </div>
                        <h5 class="text-center mb-3"><i class="fas fa-robot"></i> 京小智 · AI 助手</h5>
                        <!-- 使用内联样式确保一行显示，图片缩小 -->
                        <div style="display: flex; flex-direction: row; flex-wrap: nowrap; overflow-x: auto; gap: 1rem; justify-content: flex-start; margin: 1rem 0; padding-bottom: 0.5rem;">
                            <div class="intro-ai-card" data-ai-mode="text" style="flex: 0 0 auto; width: 100px; background: #faefe0; border-radius: 1rem; padding: 0.5rem; text-align: center; cursor: pointer; border: 1px solid #d6bc9a; transition: 0.2s;">
                                <img src="/images/AI/AI1.png" alt="文生文AI" style="width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid #b6844e; margin-bottom: 0.5rem;" onerror="this.src='https://picsum.photos/60/60?random=ai1'">
                                <div style="font-weight: 600; color: #4a2e1e; margin-bottom: 0.2rem;">文生文模式</div>
                                <div style="font-size: 0.75rem; color: #6b4f33;">非遗知识问答<br>智能语音播报</div>
                            </div>
                            <div class="intro-ai-card" data-ai-mode="image" style="flex: 0 0 auto; width: 100px; background: #faefe0; border-radius: 1rem; padding: 0.5rem; text-align: center; cursor: pointer; border: 1px solid #d6bc9a; transition: 0.2s;">
                                <img src="/images/AI/AI2.png" alt="文生图AI" style="width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid #b6844e; margin-bottom: 0.5rem;" onerror="this.src='https://picsum.photos/60/60?random=ai2'">
                                <div style="font-weight: 600; color: #4a2e1e; margin-bottom: 0.2rem;">文生图模式</div>
                                <div style="font-size: 0.75rem; color: #6b4f33;">非遗风格绘画<br>AI 创意生成</div>
                            </div>
                            <div class="intro-ai-card" data-ai-mode="video" style="flex: 0 0 auto; width: 100px; background: #faefe0; border-radius: 1rem; padding: 0.5rem; text-align: center; cursor: pointer; border: 1px solid #d6bc9a; transition: 0.2s;">
                                <img src="/images/AI/AI3.png" alt="文生视频AI" style="width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid #b6844e; margin-bottom: 0.5rem;" onerror="this.src='https://picsum.photos/60/60?random=ai3'">
                                <div style="font-weight: 600; color: #4a2e1e; margin-bottom: 0.2rem;">文生视频模式</div>
                                <div style="font-size: 0.75rem; color: #6b4f33;">非遗短视频<br>AI 智能创作</div>
                            </div>
                        </div>
                        <div class="intro-feature">
                            <i class="fas fa-lightbulb"></i> <strong>形象设计理念</strong><br>
                            京小智以“非遗传承之眼”为设计核心，三位 AI 形象分别融合京剧脸谱、景泰蓝釉色、雕漆纹理等元素，采用国风配色与圆润造型，传递“科技守护传统，智能连接文化”的愿景。
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-jingyun" data-bs-dismiss="modal">开启非遗之旅</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    const modalElement = document.getElementById('systemIntroModal');

    if (typeof bootstrap === 'undefined' || !bootstrap.Modal) {
        console.error('Bootstrap 未加载，无法显示模态框');
        alert('系统组件加载失败，请刷新页面重试。');
        return;
    }

    const modal = new bootstrap.Modal(modalElement);
    // 为 AI 卡片绑定点击跳转事件
    modalElement.querySelectorAll('.intro-ai-card').forEach(card => {
        card.addEventListener('click', () => {
            const mode = card.dataset.aiMode;
            window.location.href = `AI.html?mode=${mode}`;
        });
    });
    modal.show();
    modalElement.addEventListener('hidden.bs.modal', function() {
        modalElement.remove();
    });
};

// 绑定 Logo 点击事件（只绑定一次）
let logoBound = false;
function bindLogoClick() {
    if (logoBound) return;
    var brand = document.querySelector('.navbar-brand');
    if (brand) {
        brand.removeEventListener('click', window.showSystemIntro);
        brand.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            window.showSystemIntro();
        });
        brand.style.cursor = 'pointer';
        logoBound = true;
        console.log('Logo 绑定成功');
    } else {
        console.warn('.navbar-brand 未找到，1秒后重试');
        setTimeout(bindLogoClick, 1000);
    }
}

// 统一初始化
function initAll() {
    initTheme();
    initExitConfirm();
    bindLogoClick();
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', bindLogoClick);
    } else {
        bindLogoClick();
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAll);
} else {
    initAll();
}