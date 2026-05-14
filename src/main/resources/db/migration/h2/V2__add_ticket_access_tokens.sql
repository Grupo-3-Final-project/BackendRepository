alter table tickets add column entry_token varchar(64);
alter table tickets add column mobile_access_token varchar(64);
alter table tickets add column status varchar(255);
alter table tickets add column used_at timestamp(6);

update tickets
set entry_token = concat('ET', cast(id as varchar), replace(cast(random_uuid() as varchar), '-', '')),
    mobile_access_token = concat('MT', cast(id as varchar), replace(cast(random_uuid() as varchar), '-', '')),
    status = 'VALID'
where entry_token is null
   or mobile_access_token is null
   or status is null;

alter table tickets alter column entry_token set not null;
alter table tickets alter column mobile_access_token set not null;
alter table tickets alter column status set not null;

alter table tickets add constraint uk_tickets_entry_token unique (entry_token);
alter table tickets add constraint uk_tickets_mobile_access_token unique (mobile_access_token);
