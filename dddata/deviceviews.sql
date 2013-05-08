use ccdata;


select top 10 * from sys.views where name like 'vwDDD%'
name	object_id	principal_id	schema_id	parent_object_id	type	type_desc	create_date	modify_date	is_ms_shipped	is_published	is_schema_published	is_replicated	has_replication_filter	has_opaque_metadata	has_unchecked_assembly_data	with_check_option	is_date_correlation_view
vwDDDevice	520972132	NULL	132	0	V 	VIEW	2012-12-05 20:38:08.710	2012-12-05 20:38:08.710	0	0	0	0	0	0	0	0	0
vwDDDeviceByGroup	536972189	NULL	132	0	V 	VIEW	2012-12-05 20:38:08.803	2012-12-05 20:38:08.803	0	0	0	0	0	0	0	0	0


select top 10 * from sys.objects where object_id='520972132';
select top 10 * from sys.objects where object_id='536972189';

select definition
from sys.objects     o
join sys.sql_modules m on m.object_id = o.object_id
where o.object_id = '520972132'
  and o.type      = 'V'

Create View CCDataDelete.[vwDDDevice]
As
Select 
	  ES.ESN 
	, ES.Account_Id As AccountId 
	, ES.LastTraceCallUTC As LastCallUTC 
	, ES.UserName As Username 
	, ES.PCName As DeviceName 
	, ES.Serial As SerialNumber 
	, DM.ParsedMake As [Make] 
	, DM.ParsedModel As [Model] 
	, ES.[Asset] As [AssetNumber] 
	, OS.PCMacFlag 
	, ES.StolenFlag 
From [dbo].[ESNState] ES With (NoLock) 
Left Join [CallSummary].[vwDeviceModel] DM With (NoLock) On ES.account_id = DM.AccountId And ES.ESN = DM.ESN 
Left Join [dbo].[os_type] OS With (NoLock) On ES.ostype_id = OS.os_type_id 
Where 
	-- exclude parent esns ( > comparison is better performing, rather than <> 10)
	Len(ES.ESN) > 10 
	And ES.License_Status = 'A';


select definition
from sys.objects     o
join sys.sql_modules m on m.object_id = o.object_id
where o.object_id = '536972189'
  and o.type      = 'V'


Create View CCDataDelete.[vwDDDeviceByGroup]
As
Select 
	  ES.ESN 
	, ES.Account_Id As AccountId 
	, ES.LastTraceCallUTC As LastCallUTC 
	, ES.UserName As Username 
	, ES.PCName As DeviceName 
	, ES.Serial As SerialNumber 
	, DM.ParsedMake As [Make] 
	, DM.ParsedModel As [Model] 
	, ES.[Asset] As [AssetNumber] 
	, OS.PCMacFlag 
	, CG.Group_ID As [GroupId] 
	, ES.StolenFlag 
From [dbo].[ESNState] ES With (NoLock) 
Inner Join [CCComputerGroups].[vwComputerGroups] CG With (NoLock) On ES.Esn = CG.Esn 
Left Join [CallSummary].[vwDeviceModel] DM With (NoLock) On ES.account_id = DM.AccountId And ES.ESN = DM.ESN 
Left Join [dbo].[os_type] OS With (NoLock) On ES.ostype_id = OS.os_type_id 
Where 
	-- exclude parent esns ( > comparison is better performing, rather than <> 10) 
	Len(ES.ESN) > 10 
	And ES.License_Status = 'A';





