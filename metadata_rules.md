### eliminare:

annotations
state
file_state
associated_entities

### ridondanze:

* api - biospecimen/clinical —> file di mapping (yaml per clinical e per biospecimen). Teniamo gli attributi provenienti dalle api ("gdc" attributes)
* biospecimen - clinical —> biospecimen
* case - case.project —> case
* analytes - analytes.aliquots —> analytes.aliquots, ridondanza che non verifica nessuna delle regole decise, consideriamo analytes.aliquots che è l'attributo più specifico.
* biospecimen__admin__project_code - gdc__program__name —> gdc__program__name (aggiunto nel file biospecimen.yaml)
* gdc__disease_type - gdc__project__name - gdc__tissue_source_site__project —> gdc__disease_type (seconda scelta l'attributo più lungo). In questo caso abbiamo creato il file gdc.yaml per queta ridondanza tra attributi di tipo "gdc"

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

* manually_curated__opengdc_file_size —> dimensione in byte del file bed
* manually_curated__opengdc_file_md5 —> (che forse ora salvate solo in file aggiuntivi?) (ovvero l'md5 calcolato da voi, dato che c'è già gdc__md5 che è invece dal loro sistema)
* manually_curated__opengdc_download_date —> (ovvero la data corrispondente al vostro download da GDC)
* manually_curated__genome_built —> (o assembly: "The reference genome or assembly (such as HG19/GRCh37 or GRCh38) 

### rename:

* gruppo FILE rimane invariato
* gruppo ANALYSIS rimane invariato
* gdc__analysis__input_files__ => gdc__input_files__
* gdc__cases__ => gdc__
* gdc__cases__demographic__ => gdc__demographic__
* gdc__cases__diagnoses__ => gdc__diagnoses__
* gdc__cases__diagnoses__treatments__ => gdc__treatments__
* gdc__cases__exposures__ => gdc__exposures__
* gdc__cases__project__program__ => gdc__program__
* gdc__cases__project__ => gdc__project__
* gdc__cases__tissue_source_site__ => gdc__tissue_source_site__
* gdc__cases__samples__ => gdc__samples__
* gdc__cases__samples__portions__analytes__ => gdc__analytes__
* gdc__cases__samples__portions__analytes__aliquots__ => gdc__aliquots__
* gdc__cases__samples__portions__analytes__aliquots__center__=> gdc__center__
* gdc__cases__samples__portions__ => gdc__portions__
* gdc__cases__samples__portions__slides__ => gdc__slides__
