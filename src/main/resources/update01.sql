update Vinyl set type='CD_SAMPLER' where rubrik='CD_SAMPLER';
update Vinyl set type='LP_SAMPLER' where rubrik='LP_SAMPLER';
update Vinyl set type='CD' where rubrik='CD';
update Vinyl set type='LP' where rubrik='LP';
update Vinyl set type='SINGLES' where rubrik in ('SINGLES','SINGLES_COUNTRY_UND_JAZZ','SINGLES_DEUTSCH');
update Vinyl set type='MC' where tontraeger='MC';
update Vinyl set type='DVD' where tontraeger='DVD';
update Vinyl set type='VIDEO' where tontraeger='VIDEO';
ALTER TABLE Vinyl DROP COLUMN tontraeger, DROP COLUMN rubrik;