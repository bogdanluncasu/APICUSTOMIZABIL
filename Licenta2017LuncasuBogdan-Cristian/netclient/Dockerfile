FROM microsoft/dotnet:1.0.0-preview2-sdk

ADD ClientExpandableObjectRecogniser /ClientExpandableObjectRecogniser/
WORKDIR /ClientExpandableObjectRecogniser

RUN dotnet restore \
    && dotnet build

CMD dotnet run --server.urls http://0.0.0.0:$PORT