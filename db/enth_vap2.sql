SELECT compounds.id, compounds.idPubchem, compounds.name, hvImport.name, prop.Hvap, hvImport.hv_kcal, ref.ref_dg, hvImport.ref from compounds,hvImport,prop,ref where compounds.name like hvImport.name and compounds.id=prop.id and compounds.id=ref.id

update compounds,prop,ref set
prop.Hvap=hvImport.hv_kcal,
ref.ref_dg=hvImport.ref,
compounds.lastUpdate=CURRENT_TIMESTAMP
WHERE compounds.name like hvImport.name and compounds.id=prop.id and compounds.id=ref.id

update compounds,prop,ref set
prop.Hvap=hvImport.hv_kcal,
ref.ref_dg=hvImport.ref
WHERE compounds.name like hvImport.name and compounds.id=prop.id and compounds.id=ref.id

