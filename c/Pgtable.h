#ifndef _Pgtable_
#define _Pgtable_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "Mem.h"
#include "Cache.h"
#include "assert.h"
#include "error_code.h"
#define vAddr2PAddr 0
typedef UINT32 T_MmPageDirectoryAttrDetail;
typedef UINT32 T_MmPageTableAttrDetail;
typedef struct T_MmPageTableEntry{
UINT32 index;
T_MmPageTableAttrDetail attribute;
};
typedef struct T_MmPageDirectoryEntry{
UINT32 index;
T_MmPageDirectoryAttrDetail attribute;
Array PTE_Set;
};
typedef T_MmPageDirectoryEntry T_MmPageMiddleDirectoryEntry;
typedef struct T_MmPageDirectory{
void *page_dir_base;
Array PGD_Set;
};
typedef T_MmPageDirectory T_MmMiddlePageDirectory;
extern void findPTE(void *pgdir_base, UINT32 virt, T_MmPageTableEntry *pte);
extern void pageAttributeGet(UINT32 pVirAddr, T_MmContext *pgd, pageAttr *pAttr, errorCode *RETURN_CODE);
extern void allocMem(UINT32 addr, INT32 size, INT32 align, errorCode *RETURN_CODE);
extern void freeMem(UINT32 addr, INT32 size, INT32 align, errorCode *RETURN_CODE);
