### eliminare:

annotations
state
file_state
associated_entities

### ridondanze:

api-biospecimen/clinical —> file di mapping e teniamo api
biospecimen-clinical —> biospecimen.
case-case.project —> case
analytes-analytes.aliquots —> analytes.aliquots ridondanza che non verifica nessuna delle regole decise, consideriamo l'attributo più specifico

datetime —> teniamo solo:
* gdc__cases__samples__portions__analytes__aliquots__updated_datetime
* gdc__cases__samples__portions__analytes__aliquots__created_datetime 
* gdc__updated_datetime
* gdc__created_datetime

submitter —> teniamo solo:
* gdc__cases__samples__portions__analytes__aliquots__submitter_id
* gdc__submitter_id 

input_files —> non applicare regole ridondanza

cases.diagnoses.days_to_death e cases.diagnoses.days_to_birth sono campi deprecati (che confluiranno negli equivalenti cases.demographic.days_to_birth e cases.demographic.days_to_death --- la documentazione punta a questi). Quindi io consiglierei di includere anche i due nuovi nella vostra chiamata API e di mettere priorità a quello (tra demographic/diagnoses) che ha un valore

### creare:

manually_curated__opengdc_file_size —> dimensione in byte del file bed
