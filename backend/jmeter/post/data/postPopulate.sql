INSERT INTO USERS (ID, NAME, ROLE, USERNAME) VALUES (6969, 'Ben Dover', 'STUDENT', 'bendover69');
INSERT INTO USERS (ID, NAME, ROLE, USERNAME) VALUES (42069, 'Mike Litoris', 'TEACHER', 'mikelitoris420');
INSERT INTO COURSES (ID, NAME, TYPE) VALUES (12345, 'Cartomancia', 'EXTERNAL');
INSERT INTO QUESTIONS (ID, CONTENT, COURSE_ID, KEY, STATUS) VALUES (23764, 'A question', 12345, 6969, 'AVAILABLE');
INSERT INTO QUIZZES (ID, TITLE, TYPE) VALUES (19283, 'Facts about covid19', 'GENERATED');
INSERT INTO QUIZ_QUESTIONS (ID, QUESTION_ID, QUIZ_ID) VALUES (91020, 23764, 19283);
INSERT INTO QUIZ_ANSWERS (ID, COMPLETED, QUIZ_ID, USER_ID, USED_IN_STATISTICS, ANSWER_DATE) VALUES (10290, TRUE, 19283, 6969, FALSE, '2019-10-18');
INSERT INTO QUESTION_ANSWERS (ID, QUIZ_ANSWER_ID, QUIZ_QUESTION_ID) VALUES (77712, 10290, 91020);