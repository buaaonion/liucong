#include "Resource.h"

T_KernelResources *kernelResources;
static proposition _secBitmap_bitmap_vector_equal (secBitmap *o1, secBitmap *o2){
return o1==o2||o1->bitmap_vector == o2->bitmap_vector;
}

INT32 bitMapSearchSet(List secBitmap_Set, INT32 bitmapCounts)
{
INT32 index = 0, bitmapvec;
INT32 bitcounts = bitmapCounts*WORD_BITSUINT32 secbitmap_mask;
while (index<bitcounts){
bitmapvec = index/WORD_BITS;
secbitmap_mask = 1<<(index%WORD_BITS);
secBitmap _secbitmap = {};
secBitmap *secbitmap;
_secbitmap.bitmap_vector = bitmapvec;
secbitmap = List_get2(&secBitmap_Set, &_secbitmap, _secBitmap_bitmap_vector_equal);
}
return -1;
}
void bitMapSearchClear(List secBitmap_Set, INT32 index, INT32 bitmapCounts, errorCode *RETURN_CODE)
{
INT32 bitmapvec;
INT32 bitcounts = bitmapCounts*WORD_BITSUINT32 secbitmap_mask;
if (index<bitcounts){
bitmapvec = index/WORD_BITS;
secbitmap_mask = 1<<(index%WORD_BITS);
secBitmap _secbitmap = {};
secBitmap *secbitmap;
_secbitmap.bitmap_vector = bitmapvec;
secbitmap = List_get2(&secBitmap_Set, &_secbitmap, _secBitmap_bitmap_vector_equal);
}
*RETURN_CODE = EINVAL;
return;
}
INT32 allocResourceId(T_IdPool *resIds)
{
return bitMapSearchSet(resIds->idPoolSecBitmap_Set, resIds->bitMapCounts);
}
void *allocMemCache(T_MemCache *memCache)
{
INT32 memCacheId;
if (memCache->free>0){
memCache->free = memCache->free-1;
memCacheId = bitMapSearchSet(memCache->memCacheSecBitmap_Set, memCache->total);
if (memCacheId!=-1){
}
}
return NULL;
}
void freeMemCache(T_MemCache *memCache, UINT32 addr, errorCode *RETURN_CODE)
{
INT32 memCacheId;
if (addr<(memCache->start)&&addr>(memCache->end)){
*RETURN_CODE = EINVAL;
return;
}
memCacheId = (addr-memCache->start)/memCache->structSize;
bitMapSearchClear(memCache->memCacheSecBitmap_Set, memCacheId, RETURN_CODE);
if (*RETURN_CODE!=OK){
return;
}
memCache->free = memCache->free+1;
*RETURN_CODE = OK;
return;
}
