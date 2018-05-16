drop table wcar_provide;
drop sequence wcar_provide_seq;
drop sequence wcar_user_seq;
drop table wcar_user;


-- �����
create table wcar_user (
	usernum	number			primary key,	--����� ��ȣ
	name		varchar2(30) not null,		--��й�ȣ
	phone		varchar2(40) not null,		--����� ����(�Ϲ�,������)
	password	varchar2(20) not null,		--�� �̸�
	email		varchar2(40) 	not null,			--�� �̸���
	token		varchar2(300)
);

--����������� ����� ������
create sequence wcar_user_seq start with 1 increment by 1;



-- ��������
create table wcar_provide(
	pronum		number		primary key,	--������ȣ
	usernum	number		not null,		--����ڹ�ȣ
	pay			number		not null,		--���
	startx		number(15,10)		not null,		--���������
	starty		number(15,10)		not null,		--������浵
	destx			number(15,10)		not null,		--����������
	desty			number(15,10)		not null,		--�������浵
	request		number		default 0 not null,	--��û flag
	constraint wcar_provide_fk foreign key(usernum) 
		references wcar_user(usernum) on delete cascade
);

-- ���������� ����� ������ 
create sequence wcar_provide_seq start with 1 increment by 1;