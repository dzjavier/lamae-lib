#include <cstdlib>
#include <fstream>
#include <iostream>
#include <string>

using namespace std;

void ReadTiffTAGs(string FileName);

int main(int argc, char *argv[])
{
    string s;
    unsigned short dato=255,dato2;
    dato2 = dato<<8;
    cout<< hex<<dato2<<endl;
    cout<< "Ingrese el Nombre del archivo: "<<endl;
    cin>>s;
    ReadTiffTAGs(s);
    //system("PAUSE");
    return 0;
}
void ReadTiffTAGs(string FileName)
{
    ifstream ArchiFoto;
    char TNByteOrder[3]="  ",Tag; // Tag name: byte order
    unsigned short ArbtNumber, counter, DirNumber, Aux,ImWidth,ImLength; // Número identificador
    unsigned int OffSet,AuxInt;
    ArchiFoto.open(FileName.c_str(),ios::binary); // abro el archivo en modo binario

    if (ArchiFoto.fail() )
    {cout<< "no se pudo abrir el archivo"<<endl;
        system("PAUSE");
     exit(1);}
    cout<<sizeof(TNByteOrder[0])<<endl;
    ArchiFoto.read(TNByteOrder,2*(sizeof(TNByteOrder[0]))); // leo los dos primeros bytes
    cout<<"Byte Order: "<< TNByteOrder <<endl;
    ArchiFoto.read(reinterpret_cast<char*>(&ArbtNumber),sizeof(ArbtNumber));
    cout<<"Indentificador: "<<dec<<ArbtNumber <<endl; // (42) indentifica al archivo como formato TIF
    ArchiFoto.read(reinterpret_cast<char*>(&OffSet),(sizeof(OffSet)));
    cout<<"Offset: "<< hex << (OffSet>>24) <<endl;
    ArchiFoto.seekg(OffSet); //

    ArchiFoto.read(reinterpret_cast<char*>(&DirNumber),(sizeof(DirNumber)));
    cout<<"Numero de directorios: "<< dec << DirNumber <<endl;

    counter=1;
    while ((counter <= DirNumber))
    {
    ArchiFoto.read(reinterpret_cast<char*>(&Aux),(sizeof(Aux)));
    cout<<"TAG: "<<counter<<":"<< hex << Aux <<"  ";

    ArchiFoto.read(reinterpret_cast<char*>(&Aux),(sizeof(Aux)));
    cout<<"Tipo: "<< dec <<Aux <<" ";

    ArchiFoto.read(reinterpret_cast<char*>(&AuxInt),(sizeof(AuxInt)));
    cout<<"Count: "<< dec <<AuxInt <<" ";

    ArchiFoto.read(reinterpret_cast<char*>(&Aux),(sizeof(Aux)));
    cout<<"Valor o off-set: "<< dec <<Aux <<endl;

    ArchiFoto.seekg(2,ios::cur);

    counter++;
    }
    ArchiFoto.close();
 }
