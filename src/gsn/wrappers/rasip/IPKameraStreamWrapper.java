package gsn.wrappers.rasip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: FR33D00M
 * Date: 11.06.13.
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class IPKameraStreamWrapper {

    // FF oznacava pocetak bilo koje komande u JFIF formatu
    private static int MARKER = 0xFF;
    // Ako pri tom slijedi 0xD( znaci da se radi o pocetku slike
    private static int IMAGE_START = 0xD8;
    // Ukoliko slijedi 0xD9, utoliko zavrsava slika
    private static int IMAGE_END = 0xD9;

    private InputStream _baseStream;


    public IPKameraStreamWrapper(InputStream inputStream)
    {
        _baseStream = inputStream;
    }


    public void writeNextImage(OutputStream stream) throws IOException {
        int readValue;

        for(int i = 0; i < 1; i++)
        {
            readUntilMarker(_baseStream);

            while((readValue = _baseStream.read() )!= IMAGE_START)
            {
                readUntilMarker(_baseStream);
            }
            while((readValue = _baseStream.read() )!= IMAGE_START)
            {
                readUntilMarker(_baseStream);
            }
            while((readValue = _baseStream.read() )!= IMAGE_START)
            {
                readUntilMarker(_baseStream);
            }

            stream.write(MARKER);
            stream.write(IMAGE_START);

            while((readValue = writeUntilMarker(_baseStream, stream)) != IMAGE_END){
                stream.write(MARKER);
                stream.write(readValue);
            }

            stream.write(MARKER);
            stream.write(IMAGE_END);
        }
    }

    public void close() throws Exception {
        _baseStream.close();
    }


    private static void readUntilMarker(InputStream br) throws IOException
    {
        while((br.read()) != MARKER );
    }

    private static int writeUntilMarker(InputStream  br, OutputStream bos)  throws IOException
    {
        int readValue;
        while((readValue = br.read()) != MARKER ){
            bos.write(readValue);
        }
        readValue = br.read();
        return readValue;
    }
}
