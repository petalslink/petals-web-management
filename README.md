# Petals Web Management

Management Web console for OW2-Petals ESB ([http://petals.ow2.org](http://petals.ow2.org)) based on PlayFramework v1.

## Install & Run

Download PlayFramework >= v1.2.4 at [http://www.playframework.com/download](http://www.playframework.com/download "Download Play Framework"), add it to your path and then:

    git clone https://github.com/petalslink/petals-web-management.git
    cd petals-web-management
    play dependencies
    play run
    open http://localhost:9000

The current version has been tested with Petals ESB v4.1. You can get it from [http://petals.ow2.org/download.html](http://petals.ow2.org/download.html "Download Petals ESB")

## Develop

Using Eclipse IDE:

    play eclipsify

Will generate an Eclipse project, then import it in Eclipse.

## Doc

### Artifacts Management

- The application can host artifacts. Once uploaded, they are available in the public/artifacts folder and can be downloaded directly.
- Note that the application will check for files in this folder on start and will register ZIP files as artifacts in the local database iof they are not already available. This can be used for quick initialization.

## License

Copyright (c) 2013, Linagora
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA