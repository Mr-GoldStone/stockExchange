--company
create table if not exists company(
    id bigserial PRIMARY KEY,
    symbol varchar(255),
    company_name varchar(255),
    is_enabled boolean,
    constraint unique_name unique (company_name)
);

--stock
create table if not exists stock(
    id bigserial PRIMARY KEY,
    latestPrice real,
    change real ,
--     change NUMERIC(38,2),
    changePercent real,
    company_name VARCHAR(255) NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    unique (company_name)
--     company_id bigserial references company(id)
    );