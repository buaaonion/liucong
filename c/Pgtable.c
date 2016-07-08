#include "Pgtable.h"

static proposition _T_MmPageMiddleDirectoryEntry_index_equal (T_MmPageMiddleDirectoryEntry *o1, T_MmPageMiddleDirectoryEntry *o2){
return o1==o2||o1->index == o2->index;
}

void findPTE(void *pgdir_base, UINT32 virt, T_MmPageTableEntry *pte)
{
UINT32 pmd_index;
T_MmMiddlePageDirectory *pmd_base;
pmd_base.PGD_Set = Array_new(256);
Array *PMD_Set = Array_new(256);
UINT32 pgt_index;
pmd_base = pgdir_base;
if (pmd_base->PGD_Set==NULL){
*pte = NULL;
return;
}
PMD_Set = pmd_base->PGD_Set;
T_MmPageMiddleDirectoryEntry _pmd_entry = {};
T_MmPageMiddleDirectoryEntry *pmd_entry;
_pmd_entry.index = pmd_index;
pmd_entry = Array_get2(&PMD_Set, &_pmd_entry, _T_MmPageMiddleDirectoryEntry_index_equal);
if (pmd_entry==NULL||pmd_entry->PTE_Set==NULL){
*pte = NULL;
return;
}
pgt_index = (virt<<PTE_SHIFT)>>PMD_SHIFT;
return;
}
void pageAttributeGet(UINT32 pVirAddr, T_MmContext *pgd, pageAttr *pAttr, errorCode *RETURN_CODE)
{
T_MmPageTableEntry *pte;
if (!(VALID(pVirAddr, 0, MAX_PHYSICAL_ADDR))){
*RETURN_CODE = EINVAL;
return;
}
findPTE(pgd->page_dir_base, pVirAddr, pte);
if (pte==NULL){
*RETURN_CODE = PAGE_NOT_EXIST;
return;
}
*pAttr = (pte->attribute)&!(PAGE_MASK);
*RETURN_CODE = OK;
return;
}
void allocMem(UINT32 addr, INT32 size, INT32 align, errorCode *RETURN_CODE)
{
*RETURN_CODE = OK;
return;
}
void freeMem(UINT32 addr, INT32 size, INT32 align, errorCode *RETURN_CODE)
{
*RETURN_CODE = OK;
return;
}
