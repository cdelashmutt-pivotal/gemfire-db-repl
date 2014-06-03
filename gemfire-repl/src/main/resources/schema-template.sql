SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON

declare @origSchema varchar(30)
set @origSchema = '${schema}'

declare @origTable varchar(30)
set @origTable = '${table}'

declare @table varchar(50)
set @table = N'[' + @origSchema + '].[' + @origTable + '_Queue]'

/****** Object:  Queue Table ******/
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(@table) AND type in (N'U'))
BEGIN
declare @stmt varchar(255)
-- This is a hack to kill identity columns in our queue.
set @stmt = 'SELECT '''' as operation, * INTO ' + @table + ' FROM ' + @origTable + ' where 0=1'
+ ' union SELECT '''' as operation, * FROM ' + @origTable + ' where 0=1'

EXEC(@stmt)

END

declare @triggerName varchar(60)
set @triggerName = N'[' + @origSchema + '].[' + @origTable + '_GemFireTrigger]'

/****** Object:  Trigger ******/
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(@triggerName))
BEGIN
declare @triggerStm varchar(max)
set @triggerStm = 'CREATE TRIGGER ' + @triggerName 
   + ' ON ' + @origTable
   + ' AFTER INSERT,DELETE,UPDATE
AS 
BEGIN
	SET NOCOUNT ON;

    if EXISTS(SELECT * FROM INSERTED) AND EXISTS(SELECT * FROM DELETED)
    BEGIN
	    Insert into ' + @table
+ '		select ''U'', * from INSERTED
	END
	else if EXISTS(SELECT * FROM INSERTED)
	BEGIN
	    Insert into ' + @table
+ '		select ''I'', * from INSERTED
	END
	else if EXISTS(SELECT * FROM DELETED)
	BEGIN
	    Insert into ' + @table
+ '		select ''D'', * from DELETED
	END
END'

EXEC(@triggerStm)
END
