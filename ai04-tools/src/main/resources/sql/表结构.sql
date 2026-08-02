
CREATE TABLE `student`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_no`      varchar(50)  NOT NULL COMMENT '学号',
    `student_name`     varchar(50)  NOT NULL COMMENT '姓名',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`) USING BTREE
) ENGINE=InnoDB  COMMENT '学生表';
INSERT INTO student(student_no, student_name) VALUES
                                                  ('2024001001', '张三'),
                                                  ('2024001002', '李四'),
                                                  ('2024001003', '王五'),
                                                  ('2024001004', '赵六'),
                                                  ('2024001005', '孙七'),
                                                  ('2024001006', '周八'),
                                                  ('2024001007', '吴九'),
                                                  ('2024001008', '郑十'),
                                                  ('2024001009', '钱十一'),
                                                  ('2024001010', '冯十二');