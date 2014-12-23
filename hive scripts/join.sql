insert overwrite local directory '/home/bigdata/project/training.csv' row format delimited fields terminated by ',' select * from (select * from trainhistory t JOIN offers o ON (t.offer = o.offer)) j  RIGHT OUTER JOIN  reduced r ON (r.id = j.id AND r.chain = j.chain AND r.category = j.category AND r.brand = j.brand AND r.company = j.company);

insert overwrite local directory '/home/bigdata/project/testing.csv' row format delimited fields terminated by ',' select * from (select * from testhistory t JOIN offers o ON (t.offer = o.offer)) j  RIGHT OUTER JOIN  reduced r ON (r.id = j.id AND r.chain = j.chain AND r.category = j.category AND r.brand = j.brand AND r.company = j.company);