# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table balance (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  balance                    int(11) DEFAULT 0 ,
  canuse                     int(11) DEFAULT 0 ,
  withdraw                   int(11) DEFAULT 0 ,
  remark                     varchar(32) '' ,
  phone                      varchar(16) '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_balance primary key (id))
;

create table balance_income (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  amount                     int(11) DEFAULT 0 ,
  title                      varchar(32) '' ,
  remark                     varchar(256) '' ,
  out_trade_no               varchar(64) '' ,
  src                        varchar(16) '' ,
  phone                      varchar(16) '' ,
  sta                        varchar(2) '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_balance_income primary key (id))
;

create table balance_withdraw (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  amount                     int(11) DEFAULT 0 ,
  remark                     varchar(256) '' ,
  oper                       varchar(16) '' ,
  oper_remark                varchar(256) '' ,
  sta                        varchar(2) '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_balance_withdraw primary key (id))
;

create table certification (
  id                        bigint auto_increment not null,
  username                  varchar(16) not null,
  cardNo                    varchar(64) not null,
  address                   varchar(64),
  sex                       varchar(4),
  birthday                  varchar(32),
  `date_add`                datetime(6),
  constraint uq_certification_cardNo unique (cardNo),
  constraint pk_certification primary key (id))
;

create table certification_error (
  id                        bigint auto_increment not null,
  username                  varchar(16) not null,
  cardNo                    varchar(64) not null,
  `date_add`                datetime(6),
  constraint uq_certification_error_cardNo unique (cardNo),
  constraint pk_certification_error primary key (id))
;

create table certification_temp (
  id                        bigint auto_increment not null,
  username                  varchar(16) not null,
  cardNo                    varchar(64) not null,
  `date_add`                datetime(6),
  constraint uq_certification_temp_cardNo unique (cardNo),
  constraint pk_certification_temp primary key (id))
;

create table deleveryErrorMessage (
  id                        integer auto_increment not null,
  merchant_code              varchar(34) DEFAULT '' ,
  message                    varchar(256) DEFAULT '' ,
  decode                     varchar(34) DEFAULT '' ,
  state                      varchar(24) DEFAULT '' ,
  typ                        varchar(2) DEFAULT '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_deleveryErrorMessage primary key (id))
;

create table deviceuser (
  id                        integer auto_increment not null,
  deviceid                   varchar(64) ,
  uid                         int(11) DEFAULT 0 ,
  ostype                     varchar(2) ,
  osversion                  varchar(32) ,
  model                      varchar(32) ,
  pushToken                  varchar(32) ,
  solution                   varchar(32) ,
  appversion                 varchar(32) ,
  marketcode                 varchar(8) ,
  ver                        varchar(8) DEFAULT '',
  imei                       varchar(64) DEFAULT '',
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_deviceuser primary key (id))
;

create table magazineinfo (
  id                        integer auto_increment not null,
  magid                     integer,
  imgurl                     varchar(256) DEFAULT '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_magazineinfo primary key (id))
;

create table magazinelist (
  id                        integer auto_increment not null,
  title                      varchar(64) DEFAULT '' ,
  remark                     varchar(256) DEFAULT '' ,
  girlname                   varchar(16) DEFAULT '' ,
  girlage                    varchar(16) DEFAULT '' ,
  girlheight                 varchar(16) DEFAULT '' ,
  imgurl                     varchar(256) DEFAULT '' ,
  typ                        varchar(2) DEFAULT '0' ,
  nsort                     integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_magazinelist primary key (id))
;

create table openuser (
  id                        integer auto_increment not null,
  typ                        int(11) ,
  token                      varchar(64),
  code                       varchar(32) ,
  name                       varchar(32) ,
  address                    varchar(255) ,
  cust_id                    varchar(32) ,
  region_code                varchar(32) ,
  contact                    varchar(32) ,
  phone                      varchar(32) ,
  email                      varchar(32) ,
  dev_url                    varchar(128) ,
  prod_url                   varchar(128) ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_openuser primary key (id))
;

create table other_app (
  id                        integer auto_increment not null,
  title                      varchar(256) DEFAULT '' ,
  subtitle                   varchar(512) DEFAULT '' ,
  tips                       varchar(256) DEFAULT '' ,
  linkurl                    varchar(256) DEFAULT '' ,
  icon                       varchar(256) DEFAULT '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_other_app primary key (id))
;

create table post_order (
  id                        integer auto_increment not null,
  uid                       integer,
  ordercode                 varchar(255),
  userlong                  double,
  userlat                   double,
  username                  varchar(255),
  phone                     varchar(255),
  address                   varchar(255),
  receivelong               double,
  receivelat                double,
  receivename               varchar(255),
  receivephone              varchar(255),
  receiveaddress            varchar(255),
  subjectname               varchar(255),
  subjecttyp                integer,
  subjectremark             varchar(255),
  distance                  bigint,
  weight                    double,
  paytyp                    varchar(255),
  goodsfee                  integer,
  freight                   integer,
  totalfee                  integer,
  award                     integer,
  status                    integer,
  gettyp                    varchar(255),
  gettime                   datetime(6),
  realgettime               datetime(6),
  overtime                  datetime(6),
  date_new                  datetime(6),
  date_upd                  datetime(6),
  islooked                  varchar(255),
  ordertyp                  varchar(255),
  remark                    varchar(255),
  reasonid                  integer,
  constraint pk_post_order primary key (id))
;

create table post_order_user (
  id                        integer auto_increment not null,
  postmanid                 integer,
  orderid                   integer,
  status                    integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_post_order_user primary key (id))
;

create table postcompany (
  id                        integer auto_increment not null,
  companyname                varchar(32) DEFAULT '' ,
  companycode                varchar(34) DEFAULT '' ,
  logo                       varchar(256) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '1' ,
  ishot                      varchar(2) DEFAULT '0' ,
  nsort                      int(11) default 0 ,
  deliveryflag               varchar(2) DEFAULT '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postcompany primary key (id))
;

create table postcontent (
  id                        integer auto_increment not null,
  typ                        int(11),
  typname                    varchar(32) '' ,
  typicon                    varchar(256) '' ,
  title                      varchar(64) '' ,
  subtitle                   varchar(256) '' ,
  content                    text  ,
  amount                     int(11) DEFAULT 0 ,
  tips                       varchar(64) '' ,
  linkurl                    varchar(256) '' ,
  dateremark                 varchar(64) '' ,
  start_tim                 datetime(6),
  end_tim                   datetime(6),
  nsort                      int(11) DEFAULT 0 ,
  sta                        varchar(2) '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postcontent primary key (id))
;

create table postcontent_img (
  id                        integer auto_increment not null,
  pcid                       int(11),
  imgurl                     varchar(256) '0' ,
  date_new                  datetime(6),
  constraint pk_postcontent_img primary key (id))
;

create table postcontent_user (
  id                        integer auto_increment not null,
  pcid                       int(11),
  uid                        int(11),
  sta                        varchar(2),
  magid                      int(11),
  rewardid                   int(11),
  isnew                      varchar(2),
  date_new                  datetime(6),
  constraint pk_postcontent_user primary key (id))
;

create table postdelivery (
  id                        integer auto_increment not null,
  postman_id                integer,
  company_code              varchar(255),
  out_trade_no              varchar(255),
  mail_num                  varchar(255),
  staffid                   varchar(255),
  merchant_code             varchar(255),
  need_pay                  varchar(255),
  sender_name               varchar(255),
  sender_phone              varchar(255),
  sender_telphone           varchar(255),
  sender_province           varchar(255),
  sender_city               varchar(255),
  sender_district           varchar(255),
  sender_address            varchar(255),
  sender_company_name       varchar(255),
  sender_region_code        varchar(255),
  receiver_name             varchar(255),
  receiver_phone            varchar(255),
  receiver_telphone         varchar(255),
  receiver_province         varchar(255),
  receiver_city             varchar(255),
  receiver_district         varchar(255),
  receiver_address          varchar(255),
  receiver_company_name     varchar(255),
  receiver_region_code      varchar(255),
  goods_fee                 integer,
  goods_number              integer,
  remark                    varchar(255),
  pay_status                varchar(255),
  pay_mode                  varchar(255),
  resultmsg                 varchar(255),
  typ                       varchar(255),
  flg                       varchar(255),
  sta                       varchar(255),
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postdelivery primary key (id))
;

create table postdelivery_goods (
  id                        integer auto_increment not null,
  did                       integer,
  goodsname                 varchar(255),
  goodsnum                  integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postdelivery_goods primary key (id))
;

create table postmanuser (
  id                        integer auto_increment not null,
  staffid                    varchar(32) DEFAULT '' ,
  nickname                   varchar(16) DEFAULT '' ,
  phone                      varchar(16) DEFAULT '' ,
  headicon                   varchar(256) DEFAULT '' ,
  companyname                varchar(32) DEFAULT '' ,
  cardidno                   varchar(32) DEFAULT '' ,
  substation                 varchar(32) DEFAULT '' ,
  companyid                  int DEFAULT 0 ,
  alipay_account             varchar(64) DEFAULT '' ,
  token                      varchar(32) DEFAULT '' ,
  bbttoken                   varchar(32) DEFAULT '' ,
  lat                        numeric(9,4) ,
  lon                        numeric(9,4) ,
  height                     numeric(9,4) ,
  addr                       varchar(256) DEFAULT '' ,
  addrdes                    varchar(512) DEFAULT '' ,
  shopurl                    varchar(128) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '1' ,
  poststatus                 int DEFAULT 0 ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postmanuser primary key (id))
;

create table postmanuser_location_log (
  id                        bigint auto_increment not null,
  postmanid                 integer,
  staffid                    varchar(32) DEFAULT '' ,
  nickname                   varchar(16) DEFAULT '' ,
  phone                      varchar(16) DEFAULT '' ,
  companyname                varchar(32) DEFAULT '' ,
  substation                 varchar(32) DEFAULT '' ,
  companyid                  int DEFAULT 0 ,
  latitude                   numeric(9,4) ,
  lontitude                  numeric(9,4) ,
  height                     numeric(9,4) ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postmanuser_location_log primary key (id))
;

create table postmanuser_temp (
  id                        integer auto_increment not null,
  postmanid                 integer,
  orderid                   integer,
  constraint pk_postmanuser_temp primary key (id))
;

create table pushinfo (
  id                        integer auto_increment not null,
  title                      varchar(256) DEFAULT '' ,
  content                    varchar(512) DEFAULT '' ,
  logo                       varchar(64) DEFAULT '' ,
  logourl                    varchar(128) DEFAULT '' ,
  url                        varchar(128) DEFAULT '' ,
  pushtoken                  varchar(128) DEFAULT '' ,
  flg                        varchar(2) DEFAULT '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_pushinfo primary key (id))
;

create table reddot (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  myfav                      varchar(2) DEFAULT '0' ,
  signin                     varchar(2) DEFAULT '0' ,
  upgrade                   varchar(2) DEFAULT '0' ,
  wallet_withdraw            varchar(2) DEFAULT '0'  ,
  wallet_incoming            varchar(2) DEFAULT '0'  ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_reddot primary key (id))
;

create table smsinfo (
  id                        integer auto_increment not null,
  phone                     varchar(255),
  tpl_id                     varchar(8) DEFAULT '' ,
  args                      varchar(255),
  flg                       varchar(255),
  typ                        varchar(2) DEFAULT '1' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_smsinfo primary key (id))
;

create table user_address (
  id                        integer auto_increment not null,
  uid                       integer,
  longs                     double,
  lat                       double,
  username                  varchar(255),
  phone                     varchar(255),
  address                   varchar(255),
  typ                       integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_address primary key (id))
;

create table user_balance (
  id                        integer auto_increment not null,
  uid                       integer,
  balance                   double,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_balance primary key (id))
;

create table user_balance_log (
  id                        integer auto_increment not null,
  uid                       integer,
  changemoney               double,
  beforebalance             double,
  endbalance                double,
  remark                    varchar(255),
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_balance_log primary key (id))
;

create table user_info (
  uid                       integer auto_increment not null,
  unionid                   varchar(255),
  nickname                  varchar(255),
  typ                       integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_info primary key (uid))
;

create table versioninfo (
  id                        integer auto_increment not null,
  ostype                     varchar(2) DEFAULT '' ,
  latestver                  varchar(16) DEFAULT '' ,
  isforced                   varchar(2) DEFAULT '' ,
  remind_time                varchar(2) DEFAULT '' ,
  message                   longtext,
  url                        varchar(256) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '1' ,
  marketcode                 varchar(256) DEFAULT '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_versioninfo primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table balance;

drop table balance_income;

drop table balance_withdraw;

drop table certification;

drop table certification_error;

drop table certification_temp;

drop table deleveryErrorMessage;

drop table deviceuser;

drop table magazineinfo;

drop table magazinelist;

drop table openuser;

drop table other_app;

drop table post_order;

drop table post_order_user;

drop table postcompany;

drop table postcontent;

drop table postcontent_img;

drop table postcontent_user;

drop table postdelivery;

drop table postdelivery_goods;

drop table postmanuser;

drop table postmanuser_location_log;

drop table postmanuser_temp;

drop table pushinfo;

drop table reddot;

drop table smsinfo;

drop table user_address;

drop table user_balance;

drop table user_balance_log;

drop table user_info;

drop table versioninfo;

SET FOREIGN_KEY_CHECKS=1;

