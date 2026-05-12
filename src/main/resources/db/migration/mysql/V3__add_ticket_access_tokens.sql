alter table tickets
    add column entry_token varchar(64),
    add column mobile_access_token varchar(64),
    add column status varchar(255),
    add column used_at datetime(6);

update tickets
set entry_token = concat('ET', id, replace(uuid(), '-', '')),
    mobile_access_token = concat('MT', id, replace(uuid(), '-', '')),
    status = 'VALID'
where entry_token is null
   or mobile_access_token is null
   or status is null;

alter table tickets
    modify column entry_token varchar(64) not null,
    modify column mobile_access_token varchar(64) not null,
    modify column status varchar(255) not null;

alter table tickets add constraint UK_ticket_entry_token unique (entry_token);
alter table tickets add constraint UK_ticket_mobile_access_token unique (mobile_access_token);
