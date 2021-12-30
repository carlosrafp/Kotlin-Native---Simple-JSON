package utils

class StringReader {
    private var buf: CharArray
    private var count: Int = 0
    private var pos: Int = 0
    private var marked: Int = -1
    private var markedNbytes: Int = -1


    constructor() { // starts an empty buffer
        buf = CharArray(32)
    }

    constructor(str: String){
        buf = str.toCharArray()
        count = buf.size
    }

    constructor(ownbuf: CharArray) {  // starts the buffer from a given CharArray
        buf = ownbuf
        count = ownbuf.size
    }

    constructor(ownbuf: ByteArray){  // starts the buffer from a given ByteArray
        buf = ownbuf.decodeToString().toCharArray()
        count = ownbuf.size
    }

    fun mark (readAheadLimit: Int){
        if (readAheadLimit <= 0) throw Exception ("Illegal Argument Exception ")
        marked = pos
        markedNbytes = readAheadLimit
    }

    fun reset (){
        if (marked >= 0) pos = marked
        marked = -1
        markedNbytes = -1
    }

    private fun arraycopy(src: CharArray, srcPos: Int, dst: CharArray, dstPos: Int, len: Int) {
        for (i in 0 until len) {
            dst[dstPos + i] = src[srcPos + i]
        }
    }


    private fun expand(i: Int) {
        if (count + i <= buf.size) {
            return
        }

        val newbuf = CharArray((count + i) * 2)
        arraycopy(buf, 0, newbuf, 0, count)
        buf = newbuf
    }


    fun size(): Int {
        return count
    }

    fun available() = count - pos

    fun getPos() = pos

    fun eraseAll(){
        buf = CharArray(32)
        count = 0
        pos = 0
        marked = -1  // cancelam marcacao
        markedNbytes = -1
    }


    fun toByteArray(): ByteArray {
        //val newArray = ByteArray(count)
        //arraycopy(buf, 0, newArray, 0, count)
        //return newArray
        return  buf.toString().encodeToByteArray()
    }

    override fun toString(): String{
        return buf.toString()
    }

    fun nextChar(): Char{
        return read().toChar()
    }

    fun read(): Int{
        if (marked >= 0 && markedNbytes == 0){
            reset()
        }
        if (pos >= count)
            return 0 ////// modificacao para String ==> null character
        if (markedNbytes > 0){
            markedNbytes--
        }
        return buf[pos++].toInt()  // replace with code to newer versions
    }

    fun readString(n: Int): String{
        if (marked >= 0 && markedNbytes == 0){
            reset()
        }
        else if (marked >= 0 && n > markedNbytes) {
            throw Exception("Trying read beyond marked allowed")
        }
        if (count - pos < n)
            throw IndexOutOfBoundsException()
        pos += n
        if (marked >= 0){
            markedNbytes -= n;
        }
        return buf.concatToString(pos-n,pos)
    }

    /*
    fun read(n: Int): ByteArray{
        if (marked >= 0 && markedNbytes == 0){
            reset()
        }
        else if (marked >= 0 && n > markedNbytes) {
            return read(markedNbytes)
        }
        if (count - pos < n)
            throw IndexOutOfBoundsException()
        pos += n
        if (marked >= 0){
            markedNbytes -= n;
        }
        return buf.copyOfRange(pos-n,pos)
    }


    fun readAll(): ByteArray{
        pos = count
        marked = -1 // cancelam marcacao
        markedNbytes = -1
        return buf.copyOfRange(0,count)
    }

    fun readRemaning(): ByteArray{
        if (pos == count) return ByteArray(0)  // nothing to read
        marked = -1  // cancelam marcacao
        markedNbytes = -1
        val lastpos = pos
        pos = count
        return buf.copyOfRange(lastpos,count)
    }

    fun readFromOffset(offset: Int, count: Int): ByteArray{  // seeks from a position on buffer and changes buffer pos
        // ignora marcacao, nao muda posicao
        if (count > this.count - offset)
            throw IndexOutOfBoundsException()
        val end = offset + count
        if (count == 0) return ByteArray(0)         // use count 0 to repositioning of buffer pos
        return buf.copyOfRange(offset,end)
    }

     */

    fun seek(offset: Int){
        if (offset > count)
            throw IndexOutOfBoundsException()
        marked = -1  // cancelam marcacao
        markedNbytes = -1
        pos = offset
    }

}