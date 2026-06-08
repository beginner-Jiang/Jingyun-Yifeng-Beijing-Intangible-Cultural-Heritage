-- =====================================================
-- 京韵遗风 系统测试数据（完整版）
-- 用途：全面测试系统功能
-- 注意：请在已有的数据库结构上执行此文件，确保表结构已创建
-- =====================================================

USE beijing;

-- 1. 清理现有测试数据（谨慎执行，生产环境勿用）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE login_log;
TRUNCATE TABLE comment;
TRUNCATE TABLE recruit_application;
TRUNCATE TABLE recruit_post;
TRUNCATE TABLE activity_signup;
TRUNCATE TABLE footprint;
TRUNCATE TABLE collection;
TRUNCATE TABLE activity;
TRUNCATE TABLE heritage_site;
TRUNCATE TABLE heritage_item;
TRUNCATE TABLE inheritor;
TRUNCATE TABLE artwork;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 用户数据（密码统一为 MD5('123456') = e10adc3949ba59abbe56e057f20f883e）
-- 普通用户
INSERT INTO user (id, username, password, email, phone, age, avatar, role, interest_tags, invite_code, bio, created_at, updated_at) VALUES
(1, '张三', 'e10adc3949ba59abbe56e057f20f883e', 'zhangsan@example.com', '13800000001', 25, '/avatars/1/default.jpg', 'user', '传统技艺,民俗', NULL, '热爱传统文化，想深入了解景泰蓝', NOW(), NOW()),
(2, '李四', 'e10adc3949ba59abbe56e057f20f883e', 'lisi@example.com', '13800000002', 32, '/avatars/2/default.jpg', 'user', '曲艺,传统戏剧', NULL, '京剧爱好者，经常去剧场', NOW(), NOW()),
(3, '王五', 'e10adc3949ba59abbe56e057f20f883e', 'wangwu@example.com', '13800000003', 45, '/avatars/3/default.jpg', 'user', '传统美术,传统医药', NULL, '喜欢书画和中医药文化', NOW(), NOW()),
(4, '赵六', 'e10adc3949ba59abbe56e057f20f883e', 'zhaoliu@example.com', '13800000004', 18, '/avatars/4/default.jpg', 'user', '民间文学', NULL, '大学生，对民俗故事感兴趣', NOW(), NOW()),
(5, '孙七', 'e10adc3949ba59abbe56e057f20f883e', 'sunqi@example.com', '13800000005', 60, '/avatars/5/default.jpg', 'user', '传统技艺', NULL, '退休教师，喜欢手工艺', NOW(), NOW());

-- 传承人用户（通过邀请码升级）
INSERT INTO user (id, username, password, email, phone, age, avatar, role, interest_tags, invite_code, bio, created_at, updated_at) VALUES
(10, '钟连盛', 'e10adc3949ba59abbe56e057f20f883e', 'zhongliansheng@example.com', '13800000010', 62, '/avatars/10/default.jpg', 'inheritor', '传统技艺', 'JINGYUN-2026', '景泰蓝技艺代表性传承人', NOW(), NOW()),
(11, '李金斗', 'e10adc3949ba59abbe56e057f20f883e', 'lijindou@example.com', '13800000011', 77, '/avatars/11/default.jpg', 'inheritor', '曲艺', 'JINGYUN-2026', '著名相声表演艺术家', NOW(), NOW()),
(12, '殷秀云', 'e10adc3949ba59abbe56e057f20f883e', 'yinxiuyun@example.com', '13800000012', 71, '/avatars/12/default.jpg', 'inheritor', '传统技艺', 'JINGYUN-2026', '雕漆技艺大师', NOW(), NOW()),
(13, '郎志丽', 'e10adc3949ba59abbe56e057f20f883e', 'langzhili@example.com', '13800000013', 82, '/avatars/13/default.jpg', 'inheritor', '传统美术', 'JINGYUN-2026', '面人郎第三代传人', NOW(), NOW()),
(14, '杨玉栋', 'e10adc3949ba59abbe56e057f20f883e', 'yangyudong@example.com', '13800000014', 68, '/avatars/14/default.jpg', 'inheritor', '传统美术', 'JINGYUN-2026', '京剧脸谱制作大师', NOW(), NOW());

-- 3. 传承人档案表（关联用户ID）
INSERT INTO inheritor (id, name, level, gender, birth_year, biography, image_url, user_id) VALUES
(1, '钟连盛', '国家级', '男', 1962, '景泰蓝技艺代表性传承人，从事景泰蓝制作40余年。作品《荷梦》获中国工艺美术大师作品展金奖。', 'https://picsum.photos/200/200?random=1', 10),
(2, '李金斗', '国家级', '男', 1947, '著名相声表演艺术家，相声非遗传承人。师承赵振铎，代表作《武松打虎》。', 'https://picsum.photos/200/200?random=2', 11),
(3, '殷秀云', '国家级', '女', 1953, '雕漆技艺大师，作品多次获奖。擅长人物雕刻，作品《洛神赋》被国家博物馆收藏。', 'https://picsum.photos/200/200?random=3', 12),
(4, '郎志丽', '国家级', '女', 1942, '面人郎第三代传人，面塑艺术大师。作品《红楼梦》人物系列享誉海内外。', 'https://picsum.photos/200/200?random=4', 13),
(5, '杨玉栋', '国家级', '男', 1956, '京剧脸谱制作大师，北京非遗传承人。擅长绘制净角脸谱，作品被梅兰芳纪念馆收藏。', 'https://picsum.photos/200/200?random=5', 14);

-- 4. 非遗项目表
INSERT INTO heritage_item (id, name, category, region, level, number, description, inheritor_id, image_url) VALUES
(1, '景泰蓝制作技艺', '传统技艺', '东城区', '国家级', 'Ⅷ-1', '铜胎掐丝珐琅，燕京八绝之一，工艺繁复，色彩绚丽。制作工序包括制胎、掐丝、点蓝、烧蓝、磨光、镀金等。', 1, 'https://picsum.photos/400/200?random=101'),
(2, '京剧', '传统戏剧', '西城区', '国家级', 'Ⅳ-1', '国粹，融合唱念做打，生旦净末丑。四大徽班进京后形成，已有200多年历史。', NULL, 'https://picsum.photos/400/200?random=102'),
(3, '相声', '曲艺', '西城区', '国家级', 'Ⅴ-1', '说学逗唱，幽默机锋，京津地区深受喜爱。起源于北京天桥，代表人物侯宝林、马三立。', 2, 'https://picsum.photos/400/200?random=103'),
(4, '雕漆技艺', '传统技艺', '东城区', '国家级', 'Ⅷ-2', '在漆胎上雕刻花纹，工艺繁复，用料考究。雕漆工序包括设计、制胎、髹漆、雕刻、磨光等。', 3, 'https://picsum.photos/400/200?random=104'),
(5, '北京面人郎', '传统美术', '海淀区', '国家级', 'Ⅶ-1', '面塑艺术，细腻传神。创始人郎绍安，以捏制戏曲人物著称。', 4, 'https://picsum.photos/400/200?random=105'),
(6, '天桥中幡', '传统体育', '西城区', '国家级', 'Ⅵ-1', '民间花会中的传统体育项目，惊险壮观。幡杆高数丈，表演者用头顶、肩扛、牙咬等动作。', NULL, 'https://picsum.photos/400/200?random=106'),
(7, '厂甸庙会', '民俗', '西城区', '市级', 'Ⅹ-1', '传统春节庙会，集商贸、娱乐、祭祀于一体。始于明代，每年正月初一至初五举办。', NULL, 'https://picsum.photos/400/200?random=107'),
(8, '同仁堂中医药文化', '传统医药', '东城区', '国家级', 'Ⅸ-1', '中药炮制技艺，恪守“炮制虽繁必不敢省人工”。创建于1669年，供奉御药188年。', NULL, 'https://picsum.photos/400/200?random=108'),
(9, '京西太平鼓', '传统舞蹈', '门头沟', '国家级', 'Ⅲ-1', '祈求太平的民间舞蹈，鼓点丰富。多在春节和元宵节表演，祈求五谷丰登。', NULL, 'https://picsum.photos/400/200?random=109'),
(10, '北京宫毯织造技艺', '传统技艺', '朝阳区', '市级', 'Ⅷ-3', '皇家御用地毯，图案精美。宫毯织造需经过设计、绘图、染色、织毯、剪花等工序。', NULL, 'https://picsum.photos/400/200?random=110');

-- 5. 非遗点位表
INSERT INTO heritage_site (id, name, address, longitude, latitude, heritage_item_id, contact, open_hours, type, description, image_url) VALUES
(1, '景泰蓝博物馆', '东城区安乐林路10号', 116.410, 39.916, 1, '010-12345678', '9:00-17:00 (周一闭馆)', '博物馆', '展示景泰蓝历史与精品，可观摩制作流程。', 'https://picsum.photos/400/200?random=201'),
(2, '老舍茶馆', '西城区前门西大街正阳市场3号楼', 116.395, 39.900, NULL, '010-12345679', '10:00-22:00', '演出场所', '品茶听相声，感受京味文化。', 'https://picsum.photos/400/200?random=202'),
(3, '内联升非遗工坊', '西城区大栅栏街34号', 116.403, 39.906, NULL, '010-12345680', '9:30-20:00', '工坊', '体验手工纳底，定制专属布鞋。', 'https://picsum.photos/400/200?random=203'),
(4, '荣宝斋', '西城区琉璃厂西街19号', 116.397, 39.912, NULL, '010-12345681', '9:00-18:00', '工作室', '百年老店，木版水印和文房四宝。', 'https://picsum.photos/400/200?random=204'),
(5, '北京珐琅厂', '东城区安乐林路10号', 116.411, 39.915, 1, '010-67143672', '9:00-17:00', '工坊', '景泰蓝制作体验和展示。', 'https://picsum.photos/400/200?random=205'),
(6, '天桥印象博物馆', '西城区天桥南大街7号', 116.394, 39.883, NULL, '010-63037654', '9:00-17:00', '博物馆', '展示老天桥民俗文化。', 'https://picsum.photos/400/200?random=206'),
(7, '北京皮影剧团', '西城区前门大街', 116.398, 39.894, NULL, '010-67012345', '10:00-18:00', '演出场所', '皮影戏演出和展示。', 'https://picsum.photos/400/200?random=207'),
(8, '北京绢人工艺工作室', '朝阳区高碑店', 116.523, 39.906, NULL, '010-85765432', '9:30-17:30', '工作室', '绢人制作工艺展示。', 'https://picsum.photos/400/200?random=208'),
(9, '北京玉雕厂', '东城区光明路', 116.431, 39.879, NULL, '010-67187654', '9:00-18:00', '工坊', '玉雕技艺展示。', 'https://picsum.photos/400/200?random=209'),
(10, '北京宫毯织造技艺展示中心', '朝阳区北苑路', 116.422, 40.028, 10, '010-84923456', '9:00-17:00', '博物馆', '宫毯织造技艺展示。', 'https://picsum.photos/400/200?random=210'),
(11, '北京雕漆厂', '东城区光明路', 116.432, 39.878, 4, '010-67127890', '9:00-17:00', '工坊', '雕漆工艺制作。', 'https://picsum.photos/400/200?random=211'),
(12, '北京风筝工坊', '海淀区厂洼街', 116.297, 39.961, NULL, '010-68456789', '9:00-18:00', '工坊', '传统风筝制作。', 'https://picsum.photos/400/200?random=212'),
(13, '北京鬃人工作室', '西城区琉璃厂', 116.395, 39.910, NULL, '010-83154321', '9:30-17:30', '工作室', '鬃人艺术。', 'https://picsum.photos/400/200?random=213'),
(14, '北京毛猴工作室', '西城区南锣鼓巷', 116.404, 39.935, NULL, '010-64018765', '10:00-18:00', '工作室', '毛猴制作展示。', 'https://picsum.photos/400/200?random=214'),
(15, '北京传统音乐厅', '西城区北新华街', 116.389, 39.904, NULL, '010-66054321', '按演出时间', '演出场所', '传统音乐演出。', 'https://picsum.photos/400/200?random=215'),
(16, '北京曲艺团', '西城区前门', 116.397, 39.896, 3, '010-67023456', '按演出时间', '演出场所', '相声、鼓曲等演出。', 'https://picsum.photos/400/200?random=216'),
(17, '北京中医药大学中医药博物馆', '朝阳区北三环东路11号', 116.427, 39.978, 8, '010-64287654', '9:00-16:00', '博物馆', '中医药文物展示。', 'https://picsum.photos/400/200?random=217'),
(18, '北京民俗博物馆', '朝阳区朝阳门外大街141号', 116.447, 39.925, 7, '010-65512345', '9:00-16:30', '博物馆', '北京民俗文化。', 'https://picsum.photos/400/200?random=218'),
(19, '北京花丝镶嵌工坊', '通州区宋庄', 116.724, 39.951, NULL, '010-89567890', '9:00-18:00', '工坊', '花丝镶嵌工艺。', 'https://picsum.photos/400/200?random=219'),
(20, '北京二锅头酒传统酿造技艺展示中心', '大兴区瀛海镇', 116.435, 39.756, NULL, '010-69281234', '9:00-17:00', '博物馆', '二锅头酿造工艺展示。', 'https://picsum.photos/400/200?random=220');

-- 6. 活动表（包含已结束、进行中、待审核）
INSERT INTO activity (id, title, type, inheritor_id, site_id, start_time, end_time, max_participants, current_participants, description, image_url, status, created_at) VALUES
(1, '景泰蓝体验课', '体验课', 10, 1, DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL -5 DAY), 15, 12, '亲手体验掐丝、点蓝，制作专属小件。', 'https://picsum.photos/400/200?random=301', 2, DATE_ADD(NOW(), INTERVAL -20 DAY)),
(2, '相声开放桌', '演出', 11, 2, DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), 50, 30, '老舍茶馆相声专场，可与演员交流互动。', 'https://picsum.photos/400/200?random=302', 1, DATE_ADD(NOW(), INTERVAL -15 DAY)),
(3, '内联升纳底体验', '体验课', NULL, 3, DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), 10, 5, '学习手工纳底，感受千层底技艺。', 'https://picsum.photos/400/200?random=303', 1, DATE_ADD(NOW(), INTERVAL -10 DAY)),
(4, '京剧脸谱绘制', '体验课', 14, NULL, DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), 20, 0, '学习绘制京剧脸谱，了解脸谱文化。', 'https://picsum.photos/400/200?random=304', 1, DATE_ADD(NOW(), INTERVAL -5 DAY)),
(5, '雕漆技艺讲座', '讲座', 12, 11, DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 30, 0, '雕漆大师殷秀云分享技艺心得。', 'https://picsum.photos/400/200?random=305', 0, DATE_ADD(NOW(), INTERVAL -2 DAY)),
(6, '面塑体验工作坊', '体验课', 13, NULL, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY), 12, 0, '跟着郎志丽老师学做面人。', 'https://picsum.photos/400/200?random=306', 0, DATE_ADD(NOW(), INTERVAL -1 DAY));

-- 7. 活动报名表（部分已同意，部分待审核，部分已拒绝）
INSERT INTO activity_signup (activity_id, user_id, status, apply_time, process_time) VALUES
(2, 1, 'accepted', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL -8 DAY)),
(2, 2, 'accepted', DATE_ADD(NOW(), INTERVAL -9 DAY), DATE_ADD(NOW(), INTERVAL -7 DAY)),
(2, 3, 'pending', DATE_ADD(NOW(), INTERVAL -5 DAY), NULL),
(1, 1, 'accepted', DATE_ADD(NOW(), INTERVAL -15 DAY), DATE_ADD(NOW(), INTERVAL -12 DAY)),
(1, 4, 'rejected', DATE_ADD(NOW(), INTERVAL -14 DAY), DATE_ADD(NOW(), INTERVAL -11 DAY)),
(3, 2, 'pending', DATE_ADD(NOW(), INTERVAL -3 DAY), NULL),
(3, 5, 'pending', DATE_ADD(NOW(), INTERVAL -2 DAY), NULL),
(4, 1, 'pending', DATE_ADD(NOW(), INTERVAL -1 DAY), NULL);

-- 8. 收藏表（用户收藏项目、点位、活动）
INSERT INTO collection (user_id, target_type, target_id, created_at) VALUES
(1, 'item', 1, DATE_ADD(NOW(), INTERVAL -30 DAY)),
(1, 'item', 2, DATE_ADD(NOW(), INTERVAL -25 DAY)),
(1, 'item', 3, DATE_ADD(NOW(), INTERVAL -20 DAY)),
(1, 'site', 2, DATE_ADD(NOW(), INTERVAL -15 DAY)),
(1, 'activity', 2, DATE_ADD(NOW(), INTERVAL -10 DAY)),
(2, 'item', 1, DATE_ADD(NOW(), INTERVAL -28 DAY)),
(2, 'item', 4, DATE_ADD(NOW(), INTERVAL -22 DAY)),
(2, 'site', 1, DATE_ADD(NOW(), INTERVAL -18 DAY)),
(3, 'item', 3, DATE_ADD(NOW(), INTERVAL -26 DAY)),
(3, 'item', 5, DATE_ADD(NOW(), INTERVAL -19 DAY)),
(4, 'item', 2, DATE_ADD(NOW(), INTERVAL -24 DAY)),
(5, 'item', 1, DATE_ADD(NOW(), INTERVAL -21 DAY)),
(5, 'item', 3, DATE_ADD(NOW(), INTERVAL -17 DAY)),
(5, 'item', 6, DATE_ADD(NOW(), INTERVAL -14 DAY)),
(5, 'item', 7, DATE_ADD(NOW(), INTERVAL -12 DAY)),
(5, 'item', 8, DATE_ADD(NOW(), INTERVAL -10 DAY)),
(5, 'item', 9, DATE_ADD(NOW(), INTERVAL -8 DAY)),
(5, 'item', 10, DATE_ADD(NOW(), INTERVAL -6 DAY));

-- 9. 招募发布表
INSERT INTO recruit_post (id, inheritor_id, title, description, inheritor_name, applicant_count, max_applicants, status, created_at) VALUES
(1, 10, '景泰蓝学徒招募', '招收对景泰蓝制作感兴趣的学徒，提供系统培训。', '钟连盛', 2, 5, 0, DATE_ADD(NOW(), INTERVAL -30 DAY)),
(2, 11, '相声学员招募', '寻找热爱相声的年轻人，学习传统相声。', '李金斗', 1, 3, 0, DATE_ADD(NOW(), INTERVAL -20 DAY)),
(3, 12, '雕漆技艺传承人培养计划', '招收有志于雕漆技艺传承的学徒。', '殷秀云', 0, 4, 1, DATE_ADD(NOW(), INTERVAL -40 DAY)),
(4, 13, '面塑艺术招募', '面人郎技艺传承，招收爱好者。', '郎志丽', 1, 6, 0, DATE_ADD(NOW(), INTERVAL -15 DAY)),
(5, 14, '京剧脸谱绘制学徒', '学习脸谱绘制技艺，传承非遗。', '杨玉栋', 0, 3, 0, DATE_ADD(NOW(), INTERVAL -10 DAY));

-- 10. 招募申请表（状态 pending/accepted/rejected）
INSERT INTO recruit_application (recruit_id, user_id, status, apply_time, process_time) VALUES
(1, 1, 'accepted', DATE_ADD(NOW(), INTERVAL -25 DAY), DATE_ADD(NOW(), INTERVAL -20 DAY)),
(1, 2, 'pending', DATE_ADD(NOW(), INTERVAL -18 DAY), NULL),
(2, 3, 'accepted', DATE_ADD(NOW(), INTERVAL -15 DAY), DATE_ADD(NOW(), INTERVAL -10 DAY)),
(4, 4, 'pending', DATE_ADD(NOW(), INTERVAL -8 DAY), NULL),
(5, 5, 'pending', DATE_ADD(NOW(), INTERVAL -5 DAY), NULL);

-- 11. 论坛留言（增加大量真实评论，覆盖非遗关键词，用于词云）
INSERT INTO comment (id, user_id, content, created_at) VALUES
(1, 1, '景泰蓝太美了，希望能有更多体验活动！尤其是点蓝环节非常有趣。', DATE_ADD(NOW(), INTERVAL -20 DAY)),
(2, 2, '上周去老舍茶馆听了相声，李金斗老师的《武松打虎》太精彩了！推荐大家去。', DATE_ADD(NOW(), INTERVAL -15 DAY)),
(3, 10, '感谢大家的支持，我们会努力传承景泰蓝技艺，让更多人了解非遗。', DATE_ADD(NOW(), INTERVAL -12 DAY)),
(4, 3, '雕漆工艺令人惊叹，殷秀云老师的作品《洛神赋》细节太美了，希望有机会学习。', DATE_ADD(NOW(), INTERVAL -10 DAY)),
(5, 4, '面人郎的作品好可爱，兔儿爷和红楼梦人物都栩栩如生，想学！', DATE_ADD(NOW(), INTERVAL -8 DAY)),
(6, 11, '相声开放桌欢迎大家来玩！每周六晚上都有演出，可以现场互动。', DATE_ADD(NOW(), INTERVAL -5 DAY)),
(7, 5, '非遗地图很好用，我找到了附近的北京二锅头酒展示中心，了解了酿造工艺。', DATE_ADD(NOW(), INTERVAL -3 DAY)),
(8, 2, '京剧脸谱绘制活动什么时候再办？上次没抢到名额，好遗憾。', DATE_ADD(NOW(), INTERVAL -2 DAY)),
(9, 1, '同仁堂的中医药文化博大精深，看了展示后对中药炮制有了更深认识。', DATE_ADD(NOW(), INTERVAL -4 DAY)),
(10, 3, '京西太平鼓的表演很震撼，鼓点节奏感强，希望这种传统舞蹈能传承下去。', DATE_ADD(NOW(), INTERVAL -7 DAY)),
(11, 4, '天桥中幡太惊险了，演员用头顶几丈高的幡杆，佩服！', DATE_ADD(NOW(), INTERVAL -9 DAY)),
(12, 5, '厂甸庙会每年必去，糖葫芦、空竹、剪纸，年味十足。', DATE_ADD(NOW(), INTERVAL -11 DAY)),
(13, 10, '景泰蓝制作中的掐丝工艺需要极大耐心，欢迎来博物馆体验。', DATE_ADD(NOW(), INTERVAL -13 DAY)),
(14, 11, '相声是北京的文化符号，希望更多年轻人喜欢。', DATE_ADD(NOW(), INTERVAL -14 DAY)),
(15, 12, '雕漆的髹漆工序要反复涂刷几十层，才能进行雕刻，非常考验功力。', DATE_ADD(NOW(), INTERVAL -16 DAY)),
(16, 13, '面人郎的作品不仅在中国有名，还去过国外展览，传播中华文化。', DATE_ADD(NOW(), INTERVAL -17 DAY)),
(17, 14, '京剧脸谱的颜色都有寓意，红脸代表忠勇，白脸代表奸诈，很有意思。', DATE_ADD(NOW(), INTERVAL -18 DAY)),
(18, 1, '北京宫毯织造技艺的图案设计非常精美，融合了皇家元素。', DATE_ADD(NOW(), INTERVAL -19 DAY)),
(19, 2, '皮影戏表演很精彩，幕后的操纵者需要多年练习。', DATE_ADD(NOW(), INTERVAL -21 DAY)),
(20, 3, '花丝镶嵌工艺精细，用金银丝编织出各种图案，令人叹为观止。', DATE_ADD(NOW(), INTERVAL -22 DAY)),
(21, 4, '北京玉雕厂的玉雕作品温润细腻，螭龙璧的纹样很有古韵。', DATE_ADD(NOW(), INTERVAL -23 DAY)),
(22, 5, '北京风筝工坊的沙燕风筝很漂亮，放飞时寓意吉祥。', DATE_ADD(NOW(), INTERVAL -24 DAY)),
(23, 1, '鬃人艺术是北京独有的，用鬃毛做底座，敲击铜盘时鬃人会转动，像演戏一样。', DATE_ADD(NOW(), INTERVAL -25 DAY)),
(24, 2, '毛猴是用蝉蜕和辛夷做的，太有创意了，生动再现了老北京的生活场景。', DATE_ADD(NOW(), INTERVAL -26 DAY)),
(25, 3, '北京传统音乐厅的古琴演奏会让人心静，希望多举办这样的演出。', DATE_ADD(NOW(), INTERVAL -27 DAY)),
(26, 4, '北京曲艺团的鼓曲表演韵味十足，京韵大鼓很好听。', DATE_ADD(NOW(), INTERVAL -28 DAY)),
(27, 5, '中医药博物馆的标本很全，学到了很多养生知识。', DATE_ADD(NOW(), INTERVAL -29 DAY)),
(28, 10, '欢迎大家来景泰蓝博物馆参观，我们提供讲解服务。', DATE_ADD(NOW(), INTERVAL -30 DAY)),
(29, 11, '相声艺术需要传承，我们会继续努力培养新人。', DATE_ADD(NOW(), INTERVAL -31 DAY)),
(30, 12, '雕漆技艺是非遗瑰宝，希望有更多人关注。', DATE_ADD(NOW(), INTERVAL -32 DAY));

-- 12. 登录日志（模拟过去30天的登录记录，用于活跃时段统计）
DELIMITER $$
DROP PROCEDURE IF EXISTS generate_login_logs$$
CREATE PROCEDURE generate_login_logs()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE user_id_val INT;
    DECLARE hour_val INT;
    DECLARE day_offset INT;
    DECLARE login_time_val DATETIME;
    DECLARE done INT DEFAULT FALSE;
    DECLARE user_cursor CURSOR FOR SELECT id FROM user;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN user_cursor;
    read_loop: LOOP
        FETCH user_cursor INTO user_id_val;
        IF done THEN
            LEAVE read_loop;
        END IF;
        -- 每个用户生成最近30天内的随机登录记录，每天1-3次，随机小时
        SET day_offset = 0;
        WHILE day_offset < 30 DO
            SET i = 0;
            WHILE i < FLOOR(1 + RAND() * 3) DO
                SET hour_val = FLOOR(RAND() * 24);
                SET login_time_val = DATE_SUB(NOW(), INTERVAL day_offset DAY);
                SET login_time_val = DATE_ADD(login_time_val, INTERVAL hour_val HOUR);
                INSERT INTO login_log (user_id, login_time) VALUES (user_id_val, login_time_val);
                SET i = i + 1;
            END WHILE;
            SET day_offset = day_offset + 1;
        END WHILE;
    END LOOP;
    CLOSE user_cursor;
END$$
DELIMITER ;

CALL generate_login_logs();
DROP PROCEDURE generate_login_logs;

-- 13. 足迹表（浏览记录）
INSERT INTO footprint (user_id, target_type, target_id, action, created_at) VALUES
(1, 'item', 1, 'view', DATE_ADD(NOW(), INTERVAL -10 DAY)),
(1, 'item', 2, 'view', DATE_ADD(NOW(), INTERVAL -9 DAY)),
(1, 'site', 2, 'view', DATE_ADD(NOW(), INTERVAL -8 DAY)),
(2, 'item', 3, 'view', DATE_ADD(NOW(), INTERVAL -7 DAY)),
(2, 'activity', 2, 'signup', DATE_ADD(NOW(), INTERVAL -6 DAY)),
(3, 'item', 4, 'view', DATE_ADD(NOW(), INTERVAL -5 DAY)),
(4, 'item', 5, 'view', DATE_ADD(NOW(), INTERVAL -4 DAY)),
(5, 'item', 1, 'view', DATE_ADD(NOW(), INTERVAL -3 DAY)),
(10, 'item', 1, 'view', DATE_ADD(NOW(), INTERVAL -2 DAY)),
(11, 'item', 3, 'view', DATE_ADD(NOW(), INTERVAL -1 DAY));

-- 14. 作品表（传承人作品）
INSERT INTO artwork (id, title, description, image_url, inheritor_id, created_at) VALUES
(1, '景泰蓝·双龙瓶', '经典景泰蓝作品，双龙戏珠。瓶身以蓝色为主，龙纹采用鎏金工艺。', 'https://picsum.photos/300/200?random=401', 10, DATE_ADD(NOW(), INTERVAL -30 DAY)),
(2, '雕漆·剔红牡丹盒', '雕漆精品，牡丹花纹。盒盖雕刻盛开的牡丹，层次分明。', 'https://picsum.photos/300/200?random=402', 12, DATE_ADD(NOW(), INTERVAL -25 DAY)),
(3, '京剧脸谱·关羽', '手工绘制的关羽脸谱。红脸、卧蚕眉、丹凤眼，威风凛凛。', 'https://picsum.photos/300/200?random=403', 14, DATE_ADD(NOW(), INTERVAL -20 DAY)),
(4, '面人·兔儿爷', '面塑兔儿爷，中秋主题。兔首人身，身披金甲，憨态可掬。', 'https://picsum.photos/300/200?random=404', 13, DATE_ADD(NOW(), INTERVAL -15 DAY)),
(5, '宫毯·福寿纹', '传统宫毯图案。中心为团寿纹，四周环绕蝙蝠，寓意福寿双全。', 'https://picsum.photos/300/200?random=405', 10, DATE_ADD(NOW(), INTERVAL -10 DAY)),
(6, '玉雕·螭龙璧', '玉雕作品，螭龙纹。玉质温润，螭龙盘绕，古朴典雅。', 'https://picsum.photos/300/200?random=406', 1, DATE_ADD(NOW(), INTERVAL -5 DAY));  -- 修正为有效传承人ID

-- 15. 验证数据是否插入成功
SELECT '用户表' AS TableName, COUNT(*) FROM user
UNION SELECT '传承人档案', COUNT(*) FROM inheritor
UNION SELECT '非遗项目', COUNT(*) FROM heritage_item
UNION SELECT '非遗点位', COUNT(*) FROM heritage_site
UNION SELECT '活动', COUNT(*) FROM activity
UNION SELECT '活动报名', COUNT(*) FROM activity_signup
UNION SELECT '收藏', COUNT(*) FROM collection
UNION SELECT '招募', COUNT(*) FROM recruit_post
UNION SELECT '招募申请', COUNT(*) FROM recruit_application
UNION SELECT '留言', COUNT(*) FROM comment
UNION SELECT '登录日志', COUNT(*) FROM login_log
UNION SELECT '足迹', COUNT(*) FROM footprint
UNION SELECT '作品', COUNT(*) FROM artwork;

-- 结束