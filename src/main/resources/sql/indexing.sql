---확장 모듈 추가
create extension pg_bigm;
---title 칼럼에 bigm 인덱스 추가
create index idx_bigm_title
on post
using gin (title gin_bigm_ops);
---content 칼럼에 bigm 인덱스 추가
create index idx_bigm_content
on post
using gin (content gin_bigm_ops);
--- 칼럼에 bigm 인덱스 추가
create index idx_bigm_nickName
on member
using gin (nick_name gin_bigm_ops);
--- 칼럼에 btree 인덱스 추가
create index idx_post_member_id
on post
using gin (member_id);
