-------------------------------------------------------------------------
Copyright 2015 herd contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-------------------------------------------------------------------------

1) Create herd tables
	- herd.postgres.X.X.X.create.sql

2) Create Quartz tables. Note that this file is distributed by Quartz (v2.2.1) and is included out of convenience
	- quartz_tables_postgres.sql

3) Create Activiti tables. Note that these files are distributed by Activiti (v5.16.3.0), and are included out of convenience,
	- activiti.postgres.create.engine.sql
	- activiti.postgres.create.history.sql,
	- activiti.postgres.create.identity.sql

4) Insert reference data
	- herd.postgres.X.X.X.refdata.sql
	
5) Configure environment
	- Open herd.postgres.X.X.X.cnfgn.sql
	- Add the proper values to the parameters at the start of the file, then run

5) Upgrade the herd schema by running the relative herd upgrade scripts
	- herd.postgres.X.XX.X-to-X.XX.X.upgrade.sql
