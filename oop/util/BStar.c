#include "BStar.h"

proposition BStar_equal(BStar_Object e1, BStar_Object e2) {
	return e1 == e2;
}

proposition BStar_compare(BStar_Object e1, BStar_Object e2) {
	return e1 > e2 ? 1 : (e1 == e2 ? 0 : -1);
}

